package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.StockRequestDto
import com.example.domain.dto.StockResponseDto
import com.example.mysql.repository.product.InventoryReader
import com.example.mysql.repository.product.InventoryWriter
import org.springframework.stereotype.Service

@Service
class InventoryService(
    private val inventoryReader: InventoryReader,
    private val inventoryWriter: InventoryWriter,
) {
    fun stock(requestDto: StockRequestDto): StockResponseDto {
        val inventory = inventoryReader.findByProductId(requestDto.productId) ?: throw Exception("INVALID PRODUCT ID")
        inventory.quantity = (inventory.quantity ?: 0) + requestDto.quantity
        val inv = inventoryWriter.update(inventory)

        return StockResponseDto(inv.productId !!, (inv.quantity ?: 0))
    }
}