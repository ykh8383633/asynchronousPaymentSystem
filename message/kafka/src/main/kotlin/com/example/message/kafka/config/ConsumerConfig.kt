package com.example.message.kafka.config

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
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
@EnableConfigurationProperties(MessageProperties::class)
class ConsumerConfig(
    private val properties: MessageProperties
) {

    @Bean("DefaultConsumerFactory")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun defaultConsumerFactory(): ConsumerFactory<String, Any> {
        val groupID = properties.consumer?.groupId ?: throw Exception("Group ID can not be null") // random key to broadcast
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

    @Bean("DefaultConsumerContainer")
    @ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
    fun defaultConsumerContainer(
        @Qualifier("DefaultConsumerFactory") beanConsumerFactory: ConsumerFactory<String, Any>,
        @Qualifier("DefaultConsumer") messageListener: AcknowledgingMessageListener<String, Any>
    ): ConcurrentMessageListenerContainer<String, Any> {
        val topics = this.properties.consumer?.subscribes?.topics?.toTypedArray() ?: arrayOf()
        val containerProps = ContainerProperties(*topics)
        containerProps.messageListener = messageListener
        containerProps.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

        val container = ConcurrentMessageListenerContainer<String, Any>(beanConsumerFactory, containerProps).apply {
            concurrency = 1
        }

        return container
    }
}