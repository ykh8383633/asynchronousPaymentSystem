package com.example.mysql.entity.order

import com.example.domain.enums.OrderStatus
import com.example.domain.model.order.Order
import com.example.mysql.entity.DomainEntity
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
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var userId: Long? = null,
    var productId: Long? = null,
    var quantity: Int? = null,
    var status: OrderStatus? = null,
    var price: Long? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
): DomainEntity<Order, OrderEntity> {
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

    companion object {
        fun of(domain: Order): OrderEntity = OrderEntity(
            id = domain.id,
            userId = domain.userId,
            productId =  domain.productId,
            quantity = domain.quantity,
            status = domain.status,
            price = domain.price,
            createdDt = domain.createdDt,
            updatedDt = domain.updatedDt
        )
    }

    override fun toDomain(): Order = Order(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        status = status,
        price = price,
        createdDt = createdDt,
        updatedDt = updatedDt
    )
}