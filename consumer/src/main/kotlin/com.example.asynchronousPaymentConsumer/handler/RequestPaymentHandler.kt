package com.example.asynchronousPaymentConsumer.handler

import com.example.asynchronousPaymentConsumer.service.InventoryService
import com.example.asynchronousPaymentConsumer.service.OrderService
import com.example.asynchronousPaymentConsumer.service.PaymentService
import com.example.domain.enums.OrderRejectedReason
import com.example.domain.model.message.ConfirmOrderMessage
import com.example.domain.model.message.RejectOrderMessage
import com.example.domain.model.message.RequestOrderMessage
import com.example.domain.model.order.Order
import com.example.domain.model.payment.Payment
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.ConfirmOrder
import com.example.message.kafka.topic.RejectOrder
import com.example.message.kafka.topic.RequestOrder
import org.springframework.stereotype.Component

@Component
class RequestPaymentHandler(
    override val topic: RequestOrder,
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val producer: Producer,
    private val rejectOrderTopic: RejectOrder,
    private val confirmOrderTopic: ConfirmOrder
): GenericMessageHandlerBase<RequestOrderMessage>() {
    override fun handleMessage(data: RequestOrderMessage) {
        val orderID = data.orderId
        val order: Order = orderService.findById(orderID) ?: throw  Exception("ORDER NOT FOUND")
        val payment: Payment = paymentService.findById(data.paymentId) ?: throw Exception("PAYMENT NOT FOUND")

        val rejectOrderMessage = validateOrder(order, payment)
        if(rejectOrderMessage != null){
            producer.send(rejectOrderTopic, rejectOrderMessage)
            return;
        }

        //Thread.sleep(2000)

        producer.send(
            confirmOrderTopic,
            ConfirmOrderMessage(payment, order, data.paymentKey, data.paymentOrderId, data.amount)
        )
    }

    override fun onError(err: Exception) {
    }

    private fun validateOrder(order: Order, payment: Payment): RejectOrderMessage? {
        // calc price

        return null
    }
}