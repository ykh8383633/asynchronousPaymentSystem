package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.OrderRequestDto
import com.example.domain.enums.OrderStatus
import com.example.domain.model.message.RequestOrderMessage
import com.example.domain.model.order.Order
import com.example.mysql.repository.order.OrderWriter
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.RequestOrder
import com.example.mysql.repository.orderimport.OrderReader
import com.example.mysql.repository.product.InventoryReader
import com.example.mysql.repository.product.ProductReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderReader: OrderReader,
    private val orderWriter: OrderWriter,
    private val inventoryReader: InventoryReader,
) {
    @Transactional(value = "orderTransactionManager")
    fun order(orderRequestDto: OrderRequestDto): Order {
        val inventory = inventoryReader.findByProductId(orderRequestDto.productId) ?: throw Exception("PRODUCT NOT FOUND")

        if((inventory.quantity ?: 0) < orderRequestDto.quantity){
            throw Exception("AMOUNT IS NOT CORRECT")
        }

        val order = Order(
            productId = orderRequestDto.productId,
            userId = orderRequestDto.userId,
            quantity = orderRequestDto.quantity,
            status = OrderStatus.REQUEST
        )

        return orderWriter.save(order)
    }

    fun findAll(): MutableList<Order> = orderReader.findAll()
}