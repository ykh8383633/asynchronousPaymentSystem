package com.example.domain.model.order

import com.example.domain.enums.OrderStatus
import java.time.Instant

class Order (
    var id: Long? = null,
    var userId: Long,
    var productId: Long,
    var quantity: Int,
    var status: OrderStatus,
    var price: Long,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

}