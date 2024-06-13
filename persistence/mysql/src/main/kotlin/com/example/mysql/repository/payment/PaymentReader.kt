package com.example.mysql.repository.payment

import com.example.domain.model.payment.Payment
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class PaymentReader(
    private val repository: PaymentRepository
) {
    fun findById(id: Long): Payment? {
        val entity = repository.findById(id).getOrNull() ?: return null
        return entity.toDomain();
    }

    fun findAll(): MutableList<Payment> {
        return repository.findAll().map{it.toDomain()}.toMutableList()
    }
}