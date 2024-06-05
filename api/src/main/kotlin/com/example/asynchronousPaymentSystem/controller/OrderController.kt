package com.example.asynchronousPaymentSystem.controller

import com.example.asynchronousPaymentSystem.service.OrderService
import com.example.domain.dto.OrderRequestDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/order")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun order(@RequestBody purchaseRequestDto: OrderRequestDto): ResponseEntity<*>{
        val order = orderService.order(purchaseRequestDto)
        return ResponseEntity(order, HttpStatus.OK)
    }
}