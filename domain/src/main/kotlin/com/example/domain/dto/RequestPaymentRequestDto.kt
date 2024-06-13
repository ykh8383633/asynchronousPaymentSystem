package com.example.domain.dto

data class RequestPaymentRequestDto(
    var orderId: Long,
    var userId: Long,
    var pgOrderId: String,
    val paymentKey: String,
    val amount: Long
)
