package com.example.mysql.entity.user

import jakarta.persistence.*
import java.time.Instant
import java.util.*


@Table(name = "users")
@Entity
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null;
    var uid: String? = null
    var email: String? = null
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