package com.example.domain.dto

import com.example.domain.model.product.Product
import java.time.Instant

data class SaveProductRequestDto(
    var name: String,
    var price: Long,
    var shopId: Long,
) {
    fun toProduct(): Product {
        return Product(
            name = this.name,
            price = this.price,
            shopId = this.shopId
        )
    }
}
