package com.example.asynchronousPaymentSystem.controller

import com.example.asynchronousPaymentSystem.service.InventoryService
import com.example.domain.dto.StockRequestDto
import com.example.domain.dto.StockResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {
    @PostMapping
    fun stock(@RequestBody stockRequestDto: StockRequestDto): ResponseEntity<StockResponseDto> {
        val responseDto = inventoryService.stock(stockRequestDto)

        return ResponseEntity(responseDto, HttpStatus.OK)
    }
}