package com.example.mysql.entity.product

import com.example.domain.model.product.Product
import com.example.mysql.entity.DomainEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.*


@Table(name = "products")
@Entity
class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var shopId: Long,
    var price: Long,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
): DomainEntity<Product, ProductEntity> {
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
        fun of(domain: Product): ProductEntity = ProductEntity(
            id = domain.id,
            name = domain.name,
            price = domain.price,
            shopId = domain.shopId,
            createdDt = domain.createdDt,
            updatedDt = domain.updatedDt
        )
    }

    override fun toDomain(): Product {
        return Product (
            id = id,
            name = name,
            price = price,
            shopId = shopId,
            createdDt = createdDt,
            updatedDt = updatedDt,
        )
    }
}