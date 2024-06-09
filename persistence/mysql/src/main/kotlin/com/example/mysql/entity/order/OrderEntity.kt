package com.example.mysql.entity.order

import com.example.domain.enums.OrderStatus
import com.example.domain.model.order.Order
import com.example.mysql.entity.DomainEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "orders")
@Entity
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var userId: Long,
    var productId: Long,
    var quantity: Int,
    @Enumerated(value = EnumType.STRING)
    var status: OrderStatus,
    var price: Long,
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