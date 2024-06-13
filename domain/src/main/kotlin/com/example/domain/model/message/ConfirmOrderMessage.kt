package com.example.domain.model.message

import com.example.domain.model.order.Order
import com.example.domain.model.payment.Payment

data class ConfirmOrderMessage(
    val payment: Payment,
    val order: Order,
    val paymentKey: String,
    val paymentOrderId: String,
    val amount: Long
): Message
