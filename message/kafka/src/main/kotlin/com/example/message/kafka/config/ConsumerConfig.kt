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
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.scheduling.concurrent.CustomizableThreadFactory

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.Arrays
import java.util.concurrent.Semaphore


@Configuration
@EnableConfigurationProperties(MessageProperties::class)
class ConsumerConfig(
    private val properties: MessageProperties
) {

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val groupID = properties.consumer?.groupId ?: -1
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

    @Bean("consumerExecutor")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun consumerExecutor(): SemaphoreThreadPoolTaskExecutor {
        val executor = SemaphoreThreadPoolTaskExecutor(0)
        executor.corePoolSize = 10
        executor.maxPoolSize = 200
        executor.queueCapacity = 250
        executor.setThreadFactory(CustomizableThreadFactory("kafka-thread"))
        return executor
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun kafkaConsumerContainer(
        beanConsumerFactory: ConsumerFactory<String, Any>,
        @Qualifier("consumerExecutor") taskExecutor: ThreadPoolTaskExecutor,
        messageListener: MessageListener<String, Any>
    ): ConcurrentMessageListenerContainer<String, Any> {
        Semaphore(2).drainPermits()
        val topics = this.properties.consumer?.subscribes?.toTypedArray() ?: arrayOf()
        val containerProps = ContainerProperties(*topics)
        containerProps.listenerTaskExecutor = taskExecutor
        containerProps.messageListener = messageListener

        return ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProps).apply {
            concurrency = 4
        }
    }

}