package com.example.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    private val mapper: ObjectMapper
) {
    @Bean
    fun exchangeStrategy() : ExchangeStrategies {
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { clientCodecConfigurer ->
                clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper))
                clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(mapper))
            }.build()

        return exchangeStrategies
    }

    @Bean
    fun webClient(): WebClient =  WebClient.builder()
        .exchangeStrategies(exchangeStrategy())
        .build()
}