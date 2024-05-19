package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.SaveProductRequestDto
import com.example.domain.dto.SaveProductResponseDto
import com.example.domain.model.product.Product
import com.example.mysql.repository.product.ProductReader
import com.example.mysql.repository.product.ProductWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productReader: ProductReader,
    private val productWriter: ProductWriter
) {

    fun getAllProducts(): MutableList<Product> = productReader.findAll()
    @Transactional(value = "productTransactionManager")
    fun saveProduct(saveProductRequestDto: SaveProductRequestDto): SaveProductResponseDto {
        // validation
        if(productReader.findByNameAndShopId(saveProductRequestDto.name!!, saveProductRequestDto.shopId!!) != null) {
            throw Exception("PRODUCT_ALREADY_EXIST")
        }
        val product = productWriter.save(Product.of(saveProductRequestDto))
        return SaveProductResponseDto.of(product, true)
    }
}