package com.example.asynchronousPaymentSystem.service

import com.example.domain.model.product.Product
import com.example.mysql.repository.product.ProductReader
import com.example.mysql.repository.product.ProductWriter
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productReader: ProductReader,
    private val productWriter: ProductWriter
) {

    fun getAllProducts(): MutableList<Product> = productReader.findAll()
}