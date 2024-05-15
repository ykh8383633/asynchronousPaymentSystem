package com.example.mysql.entity

interface DomainEntity<D, E> {
    fun toDomain(): D
}