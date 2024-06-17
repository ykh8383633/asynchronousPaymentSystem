package com.example.domain.model.message

data class ThrottlingConsumerMessage(
    val permits: Int
): Message

