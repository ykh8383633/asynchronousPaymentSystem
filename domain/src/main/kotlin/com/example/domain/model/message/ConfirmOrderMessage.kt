package com.example.domain.model.message

data class ConfirmOrderMessage(
    val orderId: Long
): Message
