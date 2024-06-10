package com.example.asynchronousPaymentConsumer.config

import com.example.asynchronousPaymentConsumer.config.properties.PaymentConfigProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(PaymentConfigProperties::class)
class PaymentConfig {
}