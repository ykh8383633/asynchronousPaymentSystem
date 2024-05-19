package com.example.asynchronousPaymentSystem.controller

import com.example.domain.dto.PurchaseRequestDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/order")
class OrderController() {

    @PostMapping
    fun purchase(@RequestBody purchaseRequestDto: PurchaseRequestDto){}
}