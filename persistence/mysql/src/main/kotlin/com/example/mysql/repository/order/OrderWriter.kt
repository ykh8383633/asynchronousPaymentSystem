package com.example.mysql.repository.order

import com.example.domain.model.order.Order
import com.example.mysql.entity.order.OrderEntity
import org.springframework.stereotype.Component

@Component
class OrderWriter(
    private val orderRepository: OrderRepository
) {
    fun save(order: Order): Order {
        val entity = OrderEntity.of(order)
        return orderRepository.save(entity).toDomain()
    }

    fun update(order: Order): Order{
        val entity = OrderEntity.of(order)
        return orderRepository.save(entity).toDomain()
    }
}