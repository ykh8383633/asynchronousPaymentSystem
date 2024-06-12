package com.example.domain.model.payment

import com.example.domain.enums.PaymentStatus
import java.time.Instant

class Payment(
    var id: Long? = null,
    var orderId: Long? = null,
    var pgOrderId: String? = null,
    var pgPaymentId: String? = null,
    var reason: String? = null,
    var userId: Long? = null,
    var status: PaymentStatus? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
) {

}