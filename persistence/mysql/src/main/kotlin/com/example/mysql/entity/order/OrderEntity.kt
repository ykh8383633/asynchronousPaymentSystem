package com.example.mysql.entity.order

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Table(name = "orders")
@Entity
class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var userId: Long? = null
    var productId: Long? = null
    var quantity: Int? = null
    var price: Long? = null
    var createdDt: Instant? = null
    var updatedDt: Instant? = null

    @PrePersist
    fun prePersist() {
        if(Objects.isNull(createdDt)) {
            createdDt = Instant.now()
        }

        if(Objects.isNull(updatedDt)){
            updatedDt = Instant.now()
        }
    }

    @PreUpdate
    fun preUpdate() {
        if(Objects.isNull((updatedDt))){
            updatedDt = Instant.now()
        }
    }
}