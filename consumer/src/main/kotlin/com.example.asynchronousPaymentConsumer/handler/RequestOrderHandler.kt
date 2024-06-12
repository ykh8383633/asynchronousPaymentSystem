package com.example.asynchronousPaymentConsumer.handler

import com.example.asynchronousPaymentConsumer.service.InventoryService
import com.example.asynchronousPaymentConsumer.service.OrderService
import com.example.asynchronousPaymentConsumer.service.ProductService
import com.example.domain.enums.OrderRejectedReason
import com.example.domain.enums.OrderStatus
import com.example.domain.model.message.ConfirmOrderMessage
import com.example.domain.model.message.RejectOrderMessage
import com.example.domain.model.message.RequestOrderMessage
import com.example.domain.model.order.Order
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.ConfirmOrder
import com.example.message.kafka.topic.RejectOrder
import com.example.message.kafka.topic.RequestOrder
import com.example.mysql.repository.order.OrderWriter
import org.springframework.stereotype.Component

@Component
class RequestOrderHandler(
    override val topic: RequestOrder,
    private val orderService: OrderService,
    private val inventoryService: InventoryService,
    private val producer: Producer,
    private val rejectOrderTopic: RejectOrder,
    private val confirmOrderTopic: ConfirmOrder
): GenericMessageHandlerBase<RequestOrderMessage>() {
    override fun handleMessage(data: RequestOrderMessage) {
        val orderID = data.orderId
        val order: Order = orderService.findById(orderID) ?: throw  Exception("ORDER NOT FOUND")
        val rejectOrderMessage = validateOrder(order)
        if(rejectOrderMessage != null){
            producer.send(rejectOrderTopic, rejectOrderMessage)
            return;
        }

        producer.send(confirmOrderTopic, ConfirmOrderMessage(order, data.paymentId, data.paymentOrderId, data.amount))
    }

    private fun validateOrder(order: Order): RejectOrderMessage? {
        val inventory = inventoryService.findByProductId(order.productId)
            ?: return RejectOrderMessage(order, OrderRejectedReason.INVALID_ORDER_ID)

        if((inventory.quantity ?: 0) < order.quantity){
            return RejectOrderMessage(order, OrderRejectedReason.NOT_ENOUGH_QUANTITY)
        }

        return null
    }
}