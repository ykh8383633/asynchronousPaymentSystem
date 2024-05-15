package com.example.mysql.repository.product

import com.example.domain.model.product.Product
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class ProductReader(
    private val repository: ProductRepository
) {

    fun findById(id: Long): Product? {
        val product = repository.findById(id).getOrNull() ?: return null;
        return product.toDomain();
    }

    fun findAll(): MutableList<Product> {
        return repository.findAll()
            .map{it.toDomain()}
            .toMutableList()
    }

}