package com.example.asynchronousPaymentConsumer.handler

import com.example.domain.enums.OrderStatus
import com.example.domain.model.message.RequestOrderMessage
import com.example.domain.model.order.Order
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.topic.RequestOrder
import com.example.mysql.repository.order.OrderWriter
import org.springframework.stereotype.Component

@Component
class RequestOrderHandler(
    override val topic: RequestOrder,
    private val orderWriter: OrderWriter
): GenericMessageHandlerBase<RequestOrderMessage>() {
    override fun handleMessage(data: RequestOrderMessage) {
        println(data.order.price)
        val order = data.order;
        if(!validateOrder(order)) {
            order.status = OrderStatus.REJECTED
            orderWriter.save(order)
            return;
        }
        // TODO: 주문 검증(재고 확인 등), 결재, 주문 상태 변경
    }

    private fun validateOrder(order: Order): Boolean {
        return false
    }
}