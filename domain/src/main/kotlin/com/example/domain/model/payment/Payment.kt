package com.example.domain.model.payment

import java.time.Instant

class Payment(
    var id: Long? = null,
    var orderId: Long? = null,
    var userId: Long? = null,
    var status: String? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

}