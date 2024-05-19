package com.example.asynchronousPaymentSystem.controller

import com.example.asynchronousPaymentSystem.service.ProductService
import com.example.domain.dto.SaveProductRequestDto
import com.example.domain.dto.SaveProductResponseDto
import com.example.domain.model.product.Product
import com.example.mysql.entity.product.ProductEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/product")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/")
    fun getAllProduct(): List<Product> {
        return productService.getAllProducts()
    }

    @PostMapping
    fun saveProduct(@RequestBody saveProductRequest: SaveProductRequestDto): SaveProductResponseDto {
        return productService.saveProduct(saveProductRequest)
    }

}