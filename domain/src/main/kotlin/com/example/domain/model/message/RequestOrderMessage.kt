package com.example.domain.model.message

import com.example.domain.model.order.Order

data class RequestOrderMessage(
    val order: Order,
): Message