package com.example.mysql.entity.payment

import com.example.domain.enums.PaymentStatus
import com.example.domain.model.payment.Payment
import com.example.mysql.entity.DomainEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "payment")
@Entity
class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var pgOrderId: String? = null,
    var pgPaymentId: String? = null,
    var reason: String? = null,
    var orderId: Long? = null,
    var userId: Long? = null,
    @Enumerated(value = EnumType.STRING)
    var status: PaymentStatus? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
): DomainEntity<Payment, PaymentEntity> {
    @PrePersist
    fun prePersist() {
        if(Objects.isNull(createdDt)){
            createdDt = Instant.now()
        }

        if(Objects.isNull(updatedDt)) {
            updatedDt = Instant.now()
        }
    }

    @PreUpdate
    fun preUpdate() {
        if(Objects.isNull(updatedDt)){
            updatedDt = Instant.now()
        }
    }

    companion object {
        fun of(domain: Payment): PaymentEntity = PaymentEntity(
            id = domain.id,
            orderId = domain.orderId,
            userId =  domain.orderId,
            pgPaymentId = domain.pgPaymentId,
            pgOrderId = domain.pgOrderId,
            reason = domain.reason,
            status = domain.status,
            createdDt = domain.createdDt,
            updatedDt = domain.updatedDt
        )
    }

    override fun toDomain(): Payment {
        return Payment(
            id = id,
            orderId = orderId,
            userId = userId,
            pgPaymentId = pgPaymentId,
            pgOrderId = pgOrderId,
            reason = reason,
            status = status,
            createdDt = createdDt,
            updatedDt = updatedDt
        )
    }
}