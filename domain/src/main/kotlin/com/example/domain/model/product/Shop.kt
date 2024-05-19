package com.example.domain.model.product

import java.time.Instant

class Shop(
    var id: Long? = null,
    var name: String? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {
}