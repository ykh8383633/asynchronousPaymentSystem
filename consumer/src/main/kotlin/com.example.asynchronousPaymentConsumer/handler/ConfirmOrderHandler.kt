package com.example.asynchronousPaymentConsumer.handler

import com.example.asynchronousPaymentConsumer.config.properties.PaymentConfigProperties
import com.example.asynchronousPaymentConsumer.service.OrderService
import com.example.common.http.HttpClient
import com.example.common.http.HttpClientImpl
import com.example.domain.dto.ConfirmPaymentExceptionResponseDto
import com.example.domain.dto.ConfirmPaymentRequestDto
import com.example.domain.dto.ConfirmPaymentResponseDto
import com.example.domain.enums.OrderRejectedReason
import com.example.domain.enums.OrderStatus
import com.example.domain.model.message.ConfirmOrderMessage
import com.example.domain.model.message.RejectOrderMessage
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.ConfirmOrder
import com.example.message.kafka.topic.RejectOrder
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import java.util.*

@Component
class ConfirmOrderHandler(
    override val topic: ConfirmOrder,
    private val httpClient: HttpClient,
    private val paymentProperties: PaymentConfigProperties,
    private val producer: Producer,
    private val rejectOrder: RejectOrder,
    private val orderService: OrderService
): GenericMessageHandlerBase<ConfirmOrderMessage>(){

    override fun handleMessage(data: ConfirmOrderMessage) {
        val order = data.order

        val dto = ConfirmPaymentRequestDto(
            orderId = data.paymentOrderId,
            paymentKey = data.paymentId,
            amount = data.amount.toString()
        )
        val header = createHeader()

        httpClient.post(
            paymentProperties.confirmPaymentUrl,
            dto,
            ConfirmPaymentResponseDto::class.java,
            header
        ){ status, body ->
            println(body)
            producer.send(rejectOrder, RejectOrderMessage(order, OrderRejectedReason.PAYMENT_FAILED))
        } ?: return

        order.status = OrderStatus.CONFIRM
        orderService.update(order)

    }

    private fun createHeader(): Map<String, String> {
        val encoder = Base64.getEncoder();
        val bytes = encoder.encode((paymentProperties.secretKey + ":").toByteArray(Charsets.UTF_8))
        val authorizations = "Basic " + String(bytes)
        return mapOf(
            "Content-Type" to "application/json",
            "Authorization" to authorizations
        )
    }
}