package com.example.mysql.repository.payment

import com.example.domain.model.payment.Payment
import com.example.mysql.entity.payment.PaymentEntity
import org.springframework.stereotype.Component

@Component
class PaymentWriter(
    private val repository: PaymentRepository
) {

    fun save(payment: Payment): Payment {
        return repository.save(PaymentEntity.of(payment)).toDomain()
    }

    fun update(payment: Payment): Payment {
        return repository.save(PaymentEntity.of(payment)).toDomain()
    }

}