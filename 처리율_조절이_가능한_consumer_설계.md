# 처리율 조절(throttling)이 가능한 Consumer

## 문제 고민 동기
- 비동기 결제 시스템을 구현해 보고 Toss Payments의 결제 승인 api를 이용해 test를 해보던 중 `{"code":"TOO_MANY_REQUESTS","message":"요청량이 초과되었습니다. 일정 시간 이후 시도해주세요."}` 라는 응답을 받았다.(토스 페이먼츠의 test용 API)
> Q. 외부 API 호출, 혹은 DB의 부하 등 처리 성능을 높히는게 불가능 하거나, scale out 이 힘든  상황에는 어떻게 사용자 응답을 늦추지 않고 문제를 해결할 수 있을까? <p/>
A. Consumer 에서 처리량을 조절해 메세지를 처리하도록 설계하자!

## 처리율 조절이 가능한 Consumer
- message를 polling하여 messageListener 에게 전달하는 `consumer thread pool`과 메세지를 처리하는 `worker thread pool`을 분리
- 전달된 메세지는 `worker thread pool`에서 multi-thread로 처리 (비동기)
- `worker thread pool`은 런타임 중에 `permits`값이 조절 가능한 Semaphore를 이용해 락을 잡고 실행하도록 `SemaphoreThreadPoolTaskExecutor`를 구현하여 적용

ResizeableSemaphore.kt (module: common)
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
SemaphoreThreadPoolTaskExecutor.kt (module: common)
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

### SemaphoreThreadPoolTaskExecutor 적용

ThrottlingConsumerConfig.kt (module: message.kafka)
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
        val queueCapacity = MAX_POLL_RECORDS - maxPoolSize

        // 메세지 처리 threadPoolExecutor는 SemaphoreThreadPoolTaskExecutor 등록
        val executor = SemaphoreThreadPoolTaskExecutor(initialPermits =  maxPoolSize) 
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
        // yml 파일에 throttlingTopics로 등록된 topic만 ThrottlingConsumer 적용
        val topics = this.properties.consumer?.subscribes?.throttlingTopics?.toTypedArray() ?: arrayOf()
        val containerProps = ContainerProperties(*topics)
        // ...
        // 메세지 처리 후 즉시 수동 commit
        containerProps.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        // thread pool을 이용해 비동기 처리 시에도 메세지가 모두 처리 되기 전에 다시 polling을 하지 않기 위한 설정
        containerProps.isAsyncAcks = true

        val container = ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProps).apply {
            concurrency = CONCURRENCY
        }

        return container
    }

}
````
ThrottlingConsumer.kt (module: message.kafka)
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

## runtime 중에 permits 값을 조절하는 endpoint
- 서비스 중 consumer의 처리율을 늘리거나 줄일 수 있는 endpoint가 필요
- 요청을 받은 controller는 `command-throttling-consumer` 메세지 발행
- `command-throttling-consumer` 메세지는 모든 consumer에게 broadcast 되어야 한다.
- broadcast 되어야 하는 메세지 수신을 위한 consumer 등록

BroadcastTopicConsumerConfig.kt (module: message.kafka)
````kotlin
@Configuration
@EnableConfigurationProperties(MessageProperties::class)
class BroadcastTopicConsumerConfig(
    private val properties: MessageProperties
) {
    // ...
    @Bean("BroadcastTopicConsumerFactory")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun broadcastTopicConsumerFactory(): ConsumerFactory<String, Any> {
        // message가 broadcast 되도록 고유한 groupID 설정
        val groupID = createRandomGroupId(); 
        //...
        return DefaultKafkaConsumerFactory(config)
    }

    // ...
    
    @Bean("BroadcastTopicConsumerContainer")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun broadcastTopicConsumerContainer(
        @Qualifier("BroadcastTopicConsumerFactory") beanConsumerFactory: ConsumerFactory<String, Any>,
        @Qualifier("BroadcastTopicConsumerExecutor") taskExecutor: ThreadPoolTaskExecutor,
        @Qualifier("DefaultConsumer") messageListener: MessageListener<String, Any>
    ): ConcurrentMessageListenerContainer<String, Any> {
        // yml 파일 내 broadcastTopics로 등록된 토픽만 BroadcastConsumer 적용
        val topics = this.properties.consumer?.subscribes?.broadcastTopics?.toTypedArray() ?: arrayOf()
        val containerProps = ContainerProperties(*topics)
        // ...
        return ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProps).apply {
            concurrency = CONCURRENCY
        }
    }
}
````
ThrottlingConsumerHandler.kt (module: consumer)
````kotlin
@Component
class ThrottlingConsumerHandler(
    override val topic: ThrottlingConsumerTopic,
    @Qualifier("ThrottlingHandlerExecutor") private val throttleExecutor: SemaphoreThreadPoolTaskExecutor
): GenericMessageHandlerBase<ThrottlingConsumerMessage>() {

    override fun handleMessage(data: ThrottlingConsumerMessage) {
        // "ThrottlingHandlerExecutor" 의 permits를 조절
        val permits = throttleExecutor.addPermits(data.permits)
    }

    override fun onError(err: Exception) {
        // ...
    }
}
````

## 장점과 단점
#### 장점
- 메세지 처리를 thread pool을 이용했기 때문에 multi-thread 비동기 방식으로 더 빠르게 처리 가능해 졌다.
- 서비스의 상황에 따라 처리율을 자유롭게 조절할 수 있게 되었다.

#### 단점

- 여러개의 쓰래드로 동시 처리를 하기 때문에 offset 순서대로 메세지 처리가 완료되지 않음

    - 메세지 처리 중 서버가 다운 된다면, 메세지 처리가 완료 되었지만 어전 offset이 처리되지 않아 commit되지 않은 메세지들이 중복 처리 될 수 있다.
        - `ackmode = MANUAL_IMMEDIATE`로 설정해 순서가 맞춰진 부분까지는 즉시 commit 하도록 설정.