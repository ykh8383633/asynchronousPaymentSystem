package com.example.domain.model.message

import com.example.domain.model.order.Order

data class RequestOrderMessage(
    val paymentId: Long,
    val orderId: Long,
    val userId: Long,
    val paymentKey: String,
    val paymentOrderId: String,
    val amount: Long
): Message