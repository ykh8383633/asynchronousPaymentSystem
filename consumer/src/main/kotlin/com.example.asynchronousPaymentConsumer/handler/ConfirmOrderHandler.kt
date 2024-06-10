package com.example.asynchronousPaymentConsumer.handler

import com.example.asynchronousPaymentConsumer.config.properties.PaymentConfigProperties
import com.example.asynchronousPaymentConsumer.service.OrderService
import com.example.common.http.HttpClient
import com.example.domain.dto.ConfirmPaymentRequestDto
import com.example.domain.dto.ConfirmPaymentResponseDto
import com.example.domain.model.message.ConfirmOrderMessage
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.topic.ConfirmOrder
import org.springframework.stereotype.Component
import java.util.*

@Component
class ConfirmOrderHandler(
    override val topic: ConfirmOrder,
    private val httpClient: HttpClient,
    private val paymentProperties: PaymentConfigProperties
): GenericMessageHandlerBase<ConfirmOrderMessage>(){

    override fun handleMessage(data: ConfirmOrderMessage) {
        val order = data.order

        val dto = ConfirmPaymentRequestDto(
            orderId = order.id !!,
            paymentId = data.paymentId,
            amount = data.amount
        )
        val header = createHeader()

        val response = httpClient.post(
            paymentProperties.confirmPaymentUrl,
            dto,
            ConfirmPaymentResponseDto::class.java,
            header
        )

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