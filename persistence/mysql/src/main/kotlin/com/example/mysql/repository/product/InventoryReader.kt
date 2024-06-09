package com.example.mysql.repository.product

import com.example.domain.model.product.Inventory
import org.springframework.stereotype.Component

@Component
class InventoryReader(
    private val repository: InventoryRepository
) {
    fun findByProductId(productId: Long): Inventory? {
        val inventory = repository.findByProductId(productId) ?: return null
        return inventory.toDomain()
    }
}