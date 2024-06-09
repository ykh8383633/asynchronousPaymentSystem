package com.example.asynchronousPaymentConsumer.service

import com.example.domain.model.product.Inventory
import com.example.domain.model.product.Product
import com.example.mysql.repository.product.InventoryReader
import com.example.mysql.repository.product.InventoryWriter
import org.springframework.stereotype.Service

@Service
class InventoryService(
    private val inventoryReader: InventoryReader,
    private val inventoryWriter: InventoryWriter
) {

    fun findByProductId(productId: Long): Inventory? {
        return inventoryReader.findByProductId(productId)
    }
}