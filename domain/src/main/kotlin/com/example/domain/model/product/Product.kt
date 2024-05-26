package com.example.domain.model.product

import com.example.domain.dto.SaveProductRequestDto
import java.time.Instant

class Product (
    var id: Long? = null,
    var name: String,
    var shopId: Long,
    var price: Long,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {
}