package com.example.asynchronousPaymentSystem.controller

import com.example.asynchronousPaymentSystem.service.PaymentService
import com.example.domain.dto.RequestPaymentRequestDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/payment")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping
    fun requestConfirmPayment(@RequestBody request: RequestPaymentRequestDto): ResponseEntity<*> {
        val payment = paymentService.requestConfirmPayment(request)
        return ResponseEntity(payment, HttpStatus.OK)
    }
}