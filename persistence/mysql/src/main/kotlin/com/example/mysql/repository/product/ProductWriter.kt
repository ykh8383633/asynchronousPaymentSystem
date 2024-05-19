package com.example.mysql.repository.product

import com.example.domain.model.product.Product
import com.example.mysql.entity.product.ProductEntity
import org.springframework.stereotype.Component

@Component
class ProductWriter(
    private val repository: ProductRepository
) {
    fun save(product: Product): Product {
        val entity = repository.save(ProductEntity.of(product))
        return entity.toDomain()
    }
}