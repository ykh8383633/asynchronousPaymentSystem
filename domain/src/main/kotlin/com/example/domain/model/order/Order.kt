package com.example.domain.model.order

import com.example.domain.enums.OrderStatus
import java.time.Instant

class Order (
    var id: Long? = null,
    var userId: Long? = null,
    var productId: Long? = null,
    var quantity: Int? = null,
    var status: OrderStatus? = null,
    var price: Long? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

}