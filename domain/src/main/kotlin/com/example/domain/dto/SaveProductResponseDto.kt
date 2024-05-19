package com.example.domain.dto

import com.example.domain.model.product.Product
import java.time.Instant

data class SaveProductResponseDto(
    val saved: Boolean,
    var id: Long? = null,
    var name: String? = null,
    var price: Long? = null,
    var shopId: Long? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

    companion object {
        fun of(product: Product, saved: Boolean): SaveProductResponseDto {
            return SaveProductResponseDto(
                saved = saved,
                id = product.id,
                name = product.name,
                price = product.price,
                shopId = product.shopId,
                createdDt = product.createdDt,
                updatedDt = product.updatedDt
            )
        }
    }
}