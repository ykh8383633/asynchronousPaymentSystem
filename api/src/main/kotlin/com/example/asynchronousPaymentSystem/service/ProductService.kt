package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.SaveProductRequestDto
import com.example.domain.dto.SaveProductResponseDto
import com.example.domain.dto.StockRequestDto
import com.example.domain.dto.StockResponseDto
import com.example.domain.model.product.Inventory
import com.example.domain.model.product.Product
import com.example.mysql.repository.product.InventoryReader
import com.example.mysql.repository.product.InventoryWriter
import com.example.mysql.repository.product.ProductReader
import com.example.mysql.repository.product.ProductWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productReader: ProductReader,
    private val productWriter: ProductWriter,
    private val inventoryWriter: InventoryWriter,
) {
    fun getAllProducts(): MutableList<Product> = productReader.findAll()
    @Transactional(value = "productTransactionManager")
    fun saveProduct(saveProductRequestDto: SaveProductRequestDto): SaveProductResponseDto {
        // validation
        if(productReader.findByNameAndShopId(saveProductRequestDto.name!!, saveProductRequestDto.shopId!!) != null) {
            throw Exception("PRODUCT_ALREADY_EXIST")
        }
        val product = productWriter.save(saveProductRequestDto.toProduct())

        inventoryWriter.save(Inventory(
            productId = product.id,
            quantity = 0,
        ))

        return SaveProductResponseDto.of(product, true)
    }

    fun findById(id: Long): Product? {
        return productReader.findById((id));
    }


}