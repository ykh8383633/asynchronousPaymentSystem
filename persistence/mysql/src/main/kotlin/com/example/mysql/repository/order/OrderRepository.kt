package com.example.mysql.repository.order

import com.example.mysql.entity.order.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository: JpaRepository<OrderEntity, Long> {
}