package com.example.asynchronousPaymentConsumer.service

import com.example.domain.model.order.Order
import com.example.mysql.repository.order.OrderWriter
import com.example.mysql.repository.orderimport.OrderReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class OrderService(
    private val orderReader: OrderReader,
    private val orderWriter: OrderWriter
) {
    fun findById(id: Long): Order?{
        return orderReader.findById(id)
    }

    @Transactional(value = "orderTransactionManager")
    fun update(order: Order): Order {
        return orderWriter.save(order)
    }
}