package com.example.domain.model.message

import com.example.domain.enums.OrderRejectedReason
import com.example.domain.model.order.Order
import com.example.domain.model.payment.Payment

data class RejectOrderMessage(
    val order: Order,
    val payment: Payment,
    val rejectedReason: OrderRejectedReason
): Message
