package com.example.asynchronousPaymentConsumer.handler

import com.example.message.kafka.consumer.handler.MessageHandler
import com.example.message.kafka.topic.ConfirmOrder
import com.example.message.kafka.topic.Topic
import org.springframework.stereotype.Component

@Component
class ConfirmOrderHandler(
    override val topic: ConfirmOrder
): MessageHandler {
    init {
        println("set handler")
    }
    override fun handle(data: Any) {
        println("consume message")
    }
}