package com.example.mysql.entity.product

import com.example.domain.model.product.Inventory
import com.example.mysql.entity.DomainEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "inventory")
@Entity
class InventoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var productId: Long? = null,
    var quantity: Int? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
): DomainEntity<Inventory, InventoryEntity> {
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
        fun of(domain: Inventory): InventoryEntity = InventoryEntity(
            id = domain.id,
            productId = domain.productId,
            quantity = domain.quantity,
            createdDt = domain.createdDt,
            updatedDt = domain.updatedDt
        )
    }

    override fun toDomain(): Inventory {
        return Inventory(
            id = id,
            productId = productId,
            quantity = quantity,
            createdDt = createdDt,
            updatedDt = updatedDt
        )
    }

}