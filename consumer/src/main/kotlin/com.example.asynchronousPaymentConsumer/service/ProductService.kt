package com.example.asynchronousPaymentConsumer.service

import com.example.domain.model.product.Product
import com.example.mysql.repository.product.ProductReader
import com.example.mysql.repository.product.ProductWriter
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productReader: ProductReader,
    private val productWriter: ProductWriter
) {

    fun findById(id: Long): Product? {
        return productReader.findById(id);
    }
}