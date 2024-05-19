package com.example.domain.dto

import java.time.Instant

data class SaveProductRequestDto(
    var name: String? = null,
    var price: Long? = null,
    var shopId: Long? = null,
)
