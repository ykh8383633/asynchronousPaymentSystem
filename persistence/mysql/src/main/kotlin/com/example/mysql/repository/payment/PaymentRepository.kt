package com.example.mysql.repository.payment

import com.example.mysql.entity.payment.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository: JpaRepository<PaymentEntity, Long> {
}