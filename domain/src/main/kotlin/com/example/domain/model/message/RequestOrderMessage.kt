package com.example.domain.model.message

import com.example.domain.model.order.Order

data class RequestOrderMessage(
    val orderId: Long,
    val quantity: Int,
    val productId: Long,
    val userId: Long,
    val paymentId: String,
    val paymentOrderId: String,
    val amount: Long
): Message