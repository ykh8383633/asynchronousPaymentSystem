package com.example.domain.model.message

import com.example.domain.enums.OrderRejectedReason

data class RejectOrderMessage(
    val orderId: Long,
    val rejectedReason: OrderRejectedReason
): Message
