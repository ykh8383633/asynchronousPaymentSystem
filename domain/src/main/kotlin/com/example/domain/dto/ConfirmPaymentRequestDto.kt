package com.example.domain.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ConfirmPaymentRequestDto(
    @JsonProperty("paymentKey") val paymentKey: String,
    @JsonProperty("amount") val amount: String,
    @JsonProperty("orderId") val orderId: String
)
