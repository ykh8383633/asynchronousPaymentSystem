package com.example.domain.model.message

import com.example.domain.model.order.Order

data class ConfirmOrderMessage(
    val order: Order,
    val paymentId: String,
    val amount: Long
): Message
