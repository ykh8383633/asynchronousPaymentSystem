package com.example.mysql.repository.product

import com.example.mysql.entity.product.InventoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryRepository: JpaRepository<InventoryEntity, Long> {
}