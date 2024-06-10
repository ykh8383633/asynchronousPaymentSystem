package com.example.domain.model.message

import com.example.domain.enums.OrderRejectedReason
import com.example.domain.model.order.Order

data class RejectOrderMessage(
    val order: Order,
    val rejectedReason: OrderRejectedReason
): Message
