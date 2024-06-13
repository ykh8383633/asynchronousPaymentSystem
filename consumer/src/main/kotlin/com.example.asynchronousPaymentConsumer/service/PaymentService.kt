package com.example.asynchronousPaymentConsumer.service

import com.example.domain.model.payment.Payment
import com.example.mysql.repository.payment.PaymentReader
import com.example.mysql.repository.payment.PaymentWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentReader: PaymentReader,
    private val paymentWriter: PaymentWriter
) {
    fun findById(id: Long): Payment? {
        return paymentReader.findById(id)
    }

    @Transactional(value = "orderTransactionManager")
    fun update(payment: Payment): Payment {
        return paymentWriter.update(payment)
    }
}