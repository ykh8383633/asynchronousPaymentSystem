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
        val subscribes: MutableList<String>
    )

    data class TopicProperties(
        val confirmOrder: String
    )
}