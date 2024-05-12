package com.example.mysql.repository.user

import com.example.mysql.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity, Long> {
}