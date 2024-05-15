package com.example.mysql.repository.product

import com.example.domain.model.product.Product
import com.example.mysql.entity.product.ProductEntity
import org.springframework.stereotype.Component

@Component
class ProductWriter(
    private val repository: ProductRepository
) {
    fun save(product: Product) {
        val entity = ProductEntity.of(product)
        repository.save(entity)
    }
}