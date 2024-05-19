package com.example.mysql.entity.product

import com.example.domain.model.product.Shop
import com.example.mysql.entity.DomainEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "shops")
@Entity
class ShopEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
): DomainEntity<Shop, ShopEntity> {
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
        fun of(domain: Shop): ShopEntity {
            return ShopEntity(
                id = domain.id,
                name = domain.name,
                createdDt = domain.createdDt,
                updatedDt = domain.updatedDt
            )
        }
    }

    override fun toDomain(): Shop {
        return Shop(
            id = id,
            name = name,
            createdDt = createdDt,
            updatedDt = updatedDt
        )
    }
}