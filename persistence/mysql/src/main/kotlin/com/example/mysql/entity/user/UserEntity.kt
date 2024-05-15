package com.example.mysql.entity.user

import com.example.domain.model.user.User
import com.example.mysql.entity.DomainEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.*


@Table(name = "users")
@Entity
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var uid: String? = null,
    var email: String? = null,
    var createdDt: Instant? = null,
    var updatedDt: Instant? = null,
): DomainEntity<User, UserEntity> {


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
        fun of(domain: User): UserEntity {
            return UserEntity(
                id = domain.id,
                uid = domain.uid,
                email = domain.email,
                createdDt = domain.createdDt,
                updatedDt = domain.updatedDt
            )
        }
    }

    override fun toDomain(): User {
        return User(
            id = id,
            uid = uid,
            email = email,
            createdDt = createdDt,
            updatedDt = updatedDt,
        )
    }
}