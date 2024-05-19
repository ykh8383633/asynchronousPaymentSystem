package com.example.domain.model.product

import com.example.domain.dto.SaveProductRequestDto
import java.time.Instant

class Product (
    var id: Long? = null,
    var name: String? = null,
    var shopId: Long? = null,
    var price: Long? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {
    companion object {
        fun of(saveDto: SaveProductRequestDto): Product {
            return Product(
                name = saveDto.name,
                price = saveDto.price,
                shopId = saveDto.shopId
            )
        }
    }
}