package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.RequestPaymentRequestDto
import com.example.domain.enums.OrderStatus
import com.example.domain.enums.PaymentStatus
import com.example.domain.model.message.RequestOrderMessage
import com.example.domain.model.payment.Payment
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.RequestOrder
import com.example.mysql.repository.order.OrderWriter
import com.example.mysql.repository.orderimport.OrderReader
import com.example.mysql.repository.payment.PaymentReader
import com.example.mysql.repository.payment.PaymentWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentReader: PaymentReader,
    private val paymentWriter: PaymentWriter,
    private val orderReader: OrderReader,
    private val orderWriter: OrderWriter,
    private val producer: Producer,
    private val requestOrder: RequestOrder
) {

    @Transactional(value = "orderTransactionManager")
    fun requestConfirmPayment(request: RequestPaymentRequestDto): Payment{
        val order = orderReader.findById(request.orderId) ?: throw Exception("INVALID ORDER ID")
        order.status = OrderStatus.PAYMENT_WAITING
        order.price = request.amount
        orderWriter.update(order)

        val payment = Payment(
            orderId = order.id,
            pgPaymentId = request.paymentKey,
            pgOrderId = request.pgOrderId,
            userId = request.userId,
            status = PaymentStatus.REQUEST
        )

        val saved = paymentWriter.save(payment)

        val message = RequestOrderMessage(
            paymentId = saved.id !!,
            orderId = order.id !!,
            userId = request.userId,
            paymentKey = request.paymentKey,
            amount = request.amount,
            paymentOrderId = request.pgOrderId
        )

        producer.send(requestOrder, message)

        return saved
    }

    fun findAll(): MutableList<Payment> {
        return paymentReader.findAll()
    }
}