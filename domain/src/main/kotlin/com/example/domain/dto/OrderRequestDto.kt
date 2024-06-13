package com.example.domain.dto

data class OrderRequestDto(
    var productId: Long,
    var quantity: Int,
    var userId: Long,
)

