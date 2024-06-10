package com.example.asynchronousPaymentConsumer.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.payment")
class PaymentConfigProperties(
    val secretKey: String,
    val confirmPaymentUrl: String
) {
}