package com.example.asynchronousPaymentConsumer.handler

import com.example.asynchronousPaymentConsumer.service.OrderService
import com.example.asynchronousPaymentConsumer.service.PaymentService
import com.example.domain.enums.OrderStatus
import com.example.domain.enums.PaymentStatus
import com.example.domain.model.message.RejectOrderMessage
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.topic.RejectOrder
import org.springframework.stereotype.Component

@Component
class RejectPaymentHandler(
    override val topic: RejectOrder,
    private val orderService: OrderService,
    private val paymentService: PaymentService
): GenericMessageHandlerBase<RejectOrderMessage>() {

    override fun handleMessage(data: RejectOrderMessage) {
        val order = data.order
        val payment = data.payment

        order.status = OrderStatus.PAYMENT_REJECTED
        orderService.update(order)

        payment.status = PaymentStatus.REJECTED
        paymentService.update(payment)
    }

    override fun onError(err: Exception) {
    }
}