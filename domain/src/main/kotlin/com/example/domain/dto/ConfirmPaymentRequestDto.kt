package com.example.domain.dto

data class ConfirmPaymentRequestDto(
    val paymentId: String,
    val amount: Long,
    val orderId: Long
)
