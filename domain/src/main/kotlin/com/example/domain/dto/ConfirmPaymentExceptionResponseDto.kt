package com.example.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class ConfirmPaymentExceptionResponseDto(
    @get:JsonProperty("code") var code: String? = null,
    @get:JsonProperty("message") var message: String? = null
){}
