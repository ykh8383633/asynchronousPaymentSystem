package com.example.mysql.repository.product

import com.example.mysql.entity.product.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository: JpaRepository<ProductEntity, Long> {
    fun findByNameAndShopId(name: String, shopId: Long): ProductEntity?
}