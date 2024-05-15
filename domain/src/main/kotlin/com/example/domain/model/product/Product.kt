package com.example.domain.model.product

import java.time.Instant

class Product (
    var id: Long? = null,
    var name: String? = null,
    var price: Long? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

}