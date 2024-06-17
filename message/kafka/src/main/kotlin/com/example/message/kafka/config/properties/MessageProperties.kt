package com.example.message.kafka.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.kafka")
class MessageProperties(
    val bootstrapServer: String,
    val topics: TopicProperties,
    val consumer: ConsumerProperties?
) {
    data class ConsumerProperties(
        val groupId: String?,
        val subscribes: SubscribesProperties
    )

    data class SubscribesProperties(
        val topics: MutableList<String>,
        val throttlingTopics: MutableList<String>,
        val broadcastTopics: MutableList<String>
    )

    data class TopicProperties(
        val requestOrder: String,
        val confirmOrder: String,
        val rejectOrder: String,
        val throttlingConsumer: String
    )
}