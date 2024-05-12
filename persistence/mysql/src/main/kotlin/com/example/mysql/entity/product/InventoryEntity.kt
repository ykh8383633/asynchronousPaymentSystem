package com.example.mysql.entity.product

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "inventory")
@Entity
class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var productId: Long? = null
    var quantity: Int? = null
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