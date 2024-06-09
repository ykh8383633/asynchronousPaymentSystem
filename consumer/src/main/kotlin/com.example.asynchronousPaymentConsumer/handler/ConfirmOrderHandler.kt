package com.example.asynchronousPaymentConsumer.handler

import com.example.domain.model.message.ConfirmOrderMessage
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.topic.ConfirmOrder
import org.springframework.stereotype.Component

@Component
class ConfirmOrderHandler(
    override val topic: ConfirmOrder
): GenericMessageHandlerBase<ConfirmOrderMessage>(){

    override fun handleMessage(data: ConfirmOrderMessage) {
        println("confirmOrder ${data.orderId}")
        TODO("Not yet implemented")
    }
}