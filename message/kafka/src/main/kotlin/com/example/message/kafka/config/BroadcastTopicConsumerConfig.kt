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
import java.util.UUID
import java.util.concurrent.Semaphore


@Configuration
@EnableConfigurationProperties(MessageProperties::class)
class BroadcastTopicConsumerConfig(
    private val properties: MessageProperties
) {
    private fun createRandomGroupId(): String {
        return UUID.randomUUID().toString()
    }

    @Bean("BroadcastTopicConsumerFactory")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun broadcastTopicConsumerFactory(): ConsumerFactory<String, Any> {
        val groupID = createRandomGroupId(); // random key to broadcast
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

    @Bean("BroadcastTopicConsumerExecutor")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun broadcastTopicConsumerExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 10
        executor.queueCapacity = 25
        executor.setThreadFactory(CustomizableThreadFactory("broadcastTopic-consumer-thread"))
        return executor
    }

    @Bean("BroadcastTopicConsumerContainer")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun broadcastTopicConsumerContainer(
        @Qualifier("BroadcastTopicConsumerFactory") beanConsumerFactory: ConsumerFactory<String, Any>,
        @Qualifier("BroadcastTopicConsumerExecutor") taskExecutor: ThreadPoolTaskExecutor,
        @Qualifier("DefaultConsumer") messageListener: MessageListener<String, Any>
    ): ConcurrentMessageListenerContainer<String, Any> {
        val topics = this.properties.consumer?.subscribes?.broadcastTopics?.toTypedArray() ?: arrayOf()
        val containerProps = ContainerProperties(*topics)
        containerProps.listenerTaskExecutor = taskExecutor
        containerProps.messageListener = messageListener

        return ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProps).apply {
            concurrency = 2
        }
    }
}