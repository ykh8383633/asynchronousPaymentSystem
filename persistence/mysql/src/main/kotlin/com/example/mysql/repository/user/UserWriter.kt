package com.example.mysql.repository.user

import com.example.domain.model.user.User
import com.example.mysql.entity.user.UserEntity
import org.springframework.stereotype.Component

@Component
class UserWriter(
    private val repository: UserRepository
) {
    fun save(user: User) {
        val entity = UserEntity.of(user)
        repository.save(entity)
    }
}