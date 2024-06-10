package com.example.asynchronousPaymentConsumer.handler

import com.example.asynchronousPaymentConsumer.service.OrderService
import com.example.common.http.HttpClient
import com.example.domain.model.message.ConfirmOrderMessage
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.topic.ConfirmOrder
import org.springframework.stereotype.Component

@Component
class ConfirmOrderHandler(
    override val topic: ConfirmOrder,
    private val httpClient: HttpClient
): GenericMessageHandlerBase<ConfirmOrderMessage>(){

    override fun handleMessage(data: ConfirmOrderMessage) {
        val order = data.order
        // 결재 payment id, amount, order id를 이용해 토스 api 호출
    }
}