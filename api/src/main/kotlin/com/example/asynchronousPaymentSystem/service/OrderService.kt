package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.OrderRequestDto
import com.example.domain.enums.OrderStatus
import com.example.domain.model.order.Order
import com.example.mysql.repository.order.OrderReader
import com.example.mysql.repository.order.OrderWriter
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderReader: OrderReader,
    private val orderWriter: OrderWriter,
    private val productService: ProductService
) {
    fun order(orderRequestDto: OrderRequestDto) {
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

        // 토픽 발행
    }
}