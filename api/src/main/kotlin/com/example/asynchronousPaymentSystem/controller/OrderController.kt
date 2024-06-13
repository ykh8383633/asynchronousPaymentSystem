package com.example.asynchronousPaymentSystem.controller

import com.example.asynchronousPaymentSystem.service.OrderService
import com.example.domain.dto.OrderRequestDto
import com.example.domain.model.order.Order
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/order")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun order(@RequestBody orderRequestDto: OrderRequestDto): ResponseEntity<*>{
        val order = orderService.order(orderRequestDto)
        return ResponseEntity(order, HttpStatus.OK)
    }
    @GetMapping
    fun getAllOrders(): ResponseEntity<MutableList<Order>> {
        val orders = orderService.findAll()
        return ResponseEntity(orders, HttpStatus.OK)
    }
}