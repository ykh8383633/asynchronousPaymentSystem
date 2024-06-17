package com.example.message.kafka.config

import com.example.common.executor.SemaphoreThreadPoolTaskExecutor
import com.example.message.kafka.config.properties.MessageProperties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.scheduling.concurrent.CustomizableThreadFactory

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Semaphore


@Configuration
@EnableConfigurationProperties(MessageProperties::class)
class ThrottlingConsumerConfig(
    private val properties: MessageProperties
) {

    @Bean("ThrottlingConsumerFactory")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingConsumerFactory(): ConsumerFactory<String, Any> {
        val groupID = properties.consumer?.groupId ?: throw Exception("Group ID can not be null")
        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to properties.bootstrapServer,
            ConsumerConfig.GROUP_ID_CONFIG to groupID,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )

        return DefaultKafkaConsumerFactory(config)
    }

    @Bean("ThrottlingConsumerExecutor")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingConsumerExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.maxPoolSize = 200
        executor.queueCapacity = 250
        executor.setThreadFactory(CustomizableThreadFactory("throttling-consumer-thread"))
        return executor
    }

    @Bean("ThrottlingHandlerExecutor")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingHandlerExecutor(): SemaphoreThreadPoolTaskExecutor {
        val executor = SemaphoreThreadPoolTaskExecutor(1)
        executor.corePoolSize = 10
        executor.maxPoolSize = 200
        executor.queueCapacity = 250
        executor.setThreadFactory(CustomizableThreadFactory("throttling-handler-thread"))
        return executor
    }

    @Bean("ThrottlingConsumerContainer")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun throttlingConsumerContainer(
        @Qualifier("ThrottlingConsumerFactory") beanConsumerFactory: ConsumerFactory<String, Any>,
        @Qualifier("ThrottlingConsumerExecutor") taskExecutor: ThreadPoolTaskExecutor,
        @Qualifier("ThrottlingConsumer") messageListener: MessageListener<String, Any>
    ): ConcurrentMessageListenerContainer<String, Any> {
        val topics = this.properties.consumer?.subscribes?.throttlingTopics?.toTypedArray() ?: arrayOf()
        val containerProps = ContainerProperties(*topics)
        containerProps.listenerTaskExecutor = taskExecutor
        containerProps.messageListener = messageListener

        return ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProps).apply {
            concurrency = 4
        }
    }

}