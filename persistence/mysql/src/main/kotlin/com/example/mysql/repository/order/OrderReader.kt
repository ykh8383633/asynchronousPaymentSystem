package com.example.mysql.repository.orderimport

import com.example.domain.model.order.Order
import com.example.mysql.repository.order.OrderRepository
import org.springframework.stereotype.Component

@Component
class OrderReader(
    private val orderRepository: OrderRepository
) {
    fun findAll(): MutableList<Order>{
        return orderRepository.findAll()
            .map{it.toDomain()}
            .toMutableList()
    }
}