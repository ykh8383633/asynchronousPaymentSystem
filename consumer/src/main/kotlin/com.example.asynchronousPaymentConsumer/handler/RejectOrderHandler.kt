package com.example.asynchronousPaymentConsumer.handler

import com.example.asynchronousPaymentConsumer.service.OrderService
import com.example.domain.enums.OrderStatus
import com.example.domain.model.message.RejectOrderMessage
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.topic.RejectOrder
import com.example.message.kafka.topic.Topic
import org.springframework.stereotype.Component

@Component
class RejectOrderHandler(
    override val topic: RejectOrder,
    private val orderService: OrderService
): GenericMessageHandlerBase<RejectOrderMessage>() {

    override fun handleMessage(data: RejectOrderMessage) {
        val order = orderService.findById(data.orderId) ?: throw Exception("ORDER NOT FOUND")

        order.status = OrderStatus.REJECTED
        orderService.update(order)
    }
}