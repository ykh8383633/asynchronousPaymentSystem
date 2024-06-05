package com.example.message.kafka.topic

import com.example.message.kafka.config.properties.MessageProperties
import org.springframework.stereotype.Component

@Component
class ConfirmOrder(
    private val messageProperties: MessageProperties
): Topic {
    override val name: String = messageProperties.topics.confirmOrder
}