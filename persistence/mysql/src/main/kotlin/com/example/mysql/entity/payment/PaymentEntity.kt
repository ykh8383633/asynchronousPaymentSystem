package com.example.mysql.entity.payment

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "payment")
@Entity
class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var orderId: Long? = null
    var userId: Long? = null
    var status: String? = null
    var createdDt: Instant? = null
    var updatedDt: Instant? = null

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
}