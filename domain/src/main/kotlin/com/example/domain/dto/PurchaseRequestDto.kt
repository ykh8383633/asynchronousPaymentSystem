package com.example.domain.dto

data class PurchaseRequestDto(
    var productId: Long? = null,
    var quantity: Int? = null,
    var userId: Long? = null
)
