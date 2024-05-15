package com.example.mysql.repository.user

import com.example.domain.model.user.User
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class UserReader(
    private val repository: UserRepository
) {

    fun findById(id: Long): User? {
        val user = repository.findById(id).getOrNull() ?: return null
        return user.toDomain()
    }
}