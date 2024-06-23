## 처리율 조절(throttling)이 가능한 Consumer

### 문제 고민 동기
- 비동기 결제 시스템을 구현해 보고 Toss Payments의 결제 승인 api를 이용해 test를 해보던 중 `{"code":"TOO_MANY_REQUESTS","message":"요청량이 초과되었습니다. 일정 시간 이후 시도해주세요."}` 라는 응답을 받았다.(토스 페이먼츠의 test용 API)
> Q. 외부 API 호출, 혹은 DB의 부하 등 처리 성능을 높히는게 불가능 하거나, scale out 이 힘든  상황에는 어떻게 사용자 응답을 늦추지 않고 문제를 해결할 수 있을까? <p/>
A. Consumer 에서 처리량을 조절해 메세지를 처리하도록 설계하자!

### 처리율 조절이 가능한 Consumer
- message를 polling하여 messageListener 에게 전달하는 `consumer thread pool`과 메세지를 처리하는 `worker thread pool`을 분리
- `worker thread pool`은 런타임 중에 `permits`값이 조절 가능한 Semphore를 이용해 락을 잡고 실행하도록 `SemaphoreThreadPoolTaskExecutor`를 구현하여 적용

ResizeableSemaphore.kt (package: common)
````kotlin
class ResizeableSemaphore(
    initialPermits: Int,
    fair: Boolean = false
): Semaphore(initialPermits, fair) {
    //...

    fun resize(newPermits: Int): Int {
        lock.lock()
        try{
            val delta = newPermits - _permits
            if(delta == 0) return _permits
            if(delta > 0){
                release(delta)
            }
            else {
                reducePermits(abs(delta))
            }

            _permits += delta
            return _permits
        }
        finally {
            lock.unlock()
        }
    }

    // ...

    fun <T> submitWithRock(func: () -> T): T {
        try{
            acquire()
            return func()
        }
        finally {
            release()
        }
    }
}
````
SemaphoreThreadPoolTaskExecutor.kt (package: common)
````kotlin
class SemaphoreThreadPoolTaskExecutor(
    initialPermits: Int
): ThreadPoolTaskExecutor() {
    private val _semaphore: ResizeableSemaphore
    // ... (overrides other methods)

    override fun submit(task: Runnable): Future<*> = super.submit {
        _semaphore.submitWithRock { // 설정된 permits 만큼만 동시 실행
            task.run()
        }
    }


    fun addPermits(permits: Int): Int { // permits를 늘리거나 줄일 수 있음
        var next = _semaphore.permits + permits

        if(next < 0){
            next = 0
        }

        return _semaphore.resize(next)
    }
}
````

- SemaphoreThreadPoolTaskExecutor 적용

ThrottlingConsumerConfig.kt (package: message.kafka)
````kotlin
@Configuration
@EnableConfigurationProperties(MessageProperties::class)
class ThrottlingConsumerConfig(
    private val properties: MessageProperties
) {
    // ...

    @Bean("ThrottlingConsumerFactory")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingConsumerFactory(): ConsumerFactory<String, Any> {
        val groupID = properties.consumer?.groupId ?: throw Exception("Group ID can not be null")
        val config = mapOf<String, Any>(
            // ...
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false
        )

        return DefaultKafkaConsumerFactory(config)
    }

    @Bean("ThrottlingConsumerExecutor")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingConsumerExecutor(): ThreadPoolTaskExecutor {
        // consumerThreadPool은 기본적인 ThreadPoolTaskExecutor 등록
        val executor = ThreadPoolTaskExecutor()
        // ...
        executor.setThreadFactory(CustomizableThreadFactory("throttling-consumer-thread"))
        return executor
    }

    @Bean("ThrottlingHandlerExecutor")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingHandlerExecutor(): SemaphoreThreadPoolTaskExecutor {
        val corePoolSize = Runtime.getRuntime().availableProcessors() * 4
        val maxPoolSize = corePoolSize * 2
        val queueCapacity = 1000

        // 메세지 처리 threadPoolExecutor는 SemaphoreThreadPoolTaskExecutor 등록
        // permits 기본값: maxPoolSize
        val executor = SemaphoreThreadPoolTaskExecutor(maxPoolSize) 
        executor.corePoolSize = corePoolSize
        executor.maxPoolSize = maxPoolSize
        executor.queueCapacity = queueCapacity
        executor.setThreadFactory(CustomizableThreadFactory("throttling-handler-thread"))
        return executor
    }

    @Bean("ThrottlingConsumerContainer")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingConsumerContainer(
        @Qualifier("ThrottlingConsumerFactory") beanConsumerFactory: ConsumerFactory<String, Any>,
        @Qualifier("ThrottlingConsumerExecutor") taskExecutor: ThreadPoolTaskExecutor,
        @Qualifier("ThrottlingConsumer") messageListener: AcknowledgingMessageListener<String, Any>
    ): ConcurrentMessageListenerContainer<String, Any> {
        // yml 파일에 throttlingTopics로 등록된 topic 들에만 ThrottlingConsumer 적용
        val topics = this.properties.consumer?.subscribes?.throttlingTopics?.toTypedArray() ?: arrayOf()
        val containerProps = ContainerProperties(*topics)
        // ...
        // 메세지 처리 후 수동 commit
        containerProps.ackMode = ContainerProperties.AckMode.MANUAL
        // thread pool을 이용해 병렬 non-blocking 시에도 메세지가 모두 처리 되기 전에 다시 polling을 하지 않기 위한 설정
        containerProps.isAsyncAcks = true

        val container = ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProps).apply {
            concurrency = CONCURRENCY
        }

        return container
    }

}
````
ThrottlingConsumer.kt (package: message.kafka)
````kotlin
@Component("ThrottlingConsumer")
@ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
class ThrottlingConsumer(
    // SemaphoreThreadPoolTaskExecutor를 주입
    @Qualifier("ThrottlingHandlerExecutor") private val semaphoreTaskExecutor: SemaphoreThreadPoolTaskExecutor,
    messageProperties: MessageProperties,
    handlers: MutableList<MessageHandler>
): DefaultConsumer(
    messageProperties,
    handlers
) {
    override fun onMessage(data: ConsumerRecord<String, Any>, acknowledgment: Acknowledgment?) {
        // SemaphoreThreadPoolTaskExecutor를 이용해 실행
        semaphoreTaskExecutor.submit {
            super.onMessage(data, acknowledgment)
        }
    }
}
````
