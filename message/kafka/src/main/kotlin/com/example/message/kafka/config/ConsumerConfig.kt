package com.example.message.kafka.config

import com.example.message.kafka.config.properties.MessageProperties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.scheduling.concurrent.CustomizableThreadFactory

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor




@Configuration
class ConsumerConfig(
    private val properties: MessageProperties
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to properties.bootstrapServer,
            ConsumerConfig.GROUP_ID_CONFIG to properties.consumer.groupId,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )

        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun defaultConsumerExecutor(): ThreadPoolTaskExecutor {
        val isNotConsumer = this.properties.consumer.subscribes.isEmpty()
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = if(isNotConsumer) 0 else 10
        executor.maxPoolSize = if(isNotConsumer) 0 else 200
        executor.queueCapacity = if(isNotConsumer) 0 else 250
        executor.setThreadFactory(CustomizableThreadFactory("kafka-thread"))
        return executor
    }

    @Bean
    fun kafkaConsumerContainer(
        beanConsumerFactory: ConsumerFactory<String, Any>,
        taskExecutor: ThreadPoolTaskExecutor
    ): ConcurrentMessageListenerContainer<String, Any> {
        val topics = this.properties.consumer.subscribes.toTypedArray<String>()
        val containerProperties = ContainerProperties(*topics)
        containerProperties.listenerTaskExecutor = taskExecutor
        return ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProperties)
    }

}