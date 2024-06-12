package com.example.domain.model.product

import java.time.Instant

class Inventory(
    var id: Long? = null,
    var productId: Long? = null,
    var quantity: Long? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

}