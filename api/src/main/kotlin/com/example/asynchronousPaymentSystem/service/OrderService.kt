package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.OrderRequestDto
import com.example.domain.enums.OrderStatus
import com.example.domain.model.message.ConfirmOrderMessage
import com.example.domain.model.order.Order
import com.example.mysql.repository.order.OrderReader
import com.example.mysql.repository.order.OrderWriter
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.ConfirmOrder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderReader: OrderReader,
    private val orderWriter: OrderWriter,
    private val productService: ProductService,
    private val producer: Producer,
    private val confirmOrder: ConfirmOrder
) {
    @Transactional(value = "orderTransactionManager")
    fun order(orderRequestDto: OrderRequestDto): Order {
        val product = productService.findById(orderRequestDto.productId) ?: throw Exception("INVALID_PRODUCT")
        val price = orderRequestDto.quantity * product.price
        val order = Order(
            productId = orderRequestDto.productId,
            userId = orderRequestDto.userId,
            quantity = orderRequestDto.quantity,
            price = price,
            status = OrderStatus.REQUEST
        )

        val saved = orderWriter.save(order)

        val message = ConfirmOrderMessage(saved)
        producer.send(confirmOrder, message)

        return saved
    }
}