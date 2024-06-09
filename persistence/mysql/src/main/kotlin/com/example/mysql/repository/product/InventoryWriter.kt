package com.example.mysql.repository.product

import com.example.domain.model.product.Inventory
import com.example.mysql.entity.product.InventoryEntity
import org.springframework.stereotype.Component

@Component
class InventoryWriter(
    private val repository: InventoryRepository
) {
    fun save(inventory: Inventory): Inventory {
        return repository.save(InventoryEntity.of(inventory)).toDomain()
    }
}