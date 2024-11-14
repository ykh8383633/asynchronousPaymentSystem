package com.example.domain.model.message

interface OrderedMessage: Message {
    val isOrdered: Boolean
}