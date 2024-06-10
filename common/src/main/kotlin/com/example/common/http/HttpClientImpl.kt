package com.example.common.http

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class HttpClientImpl(
    private val webClient: WebClient
): HttpClient {

    override fun <TResult> get(url: String, clazz: Class<TResult>): TResult? = get(url, clazz, mapOf())

    override fun <TResult> get(url: String, clazz: Class<TResult>, headers: Map<String, String>): TResult? {
        return webClient.mutate()
            .baseUrl(url)
            .build()
            .get()
            .headers { header -> headers.forEach(header::add) }
            .retrieve()
            .bodyToMono(clazz)
            .block()
    }

    override fun <TBody, TResult> post(url: String, body: TBody, clazz: Class<TResult>): TResult? {
        return post(url, body, clazz, mapOf())
    }

    override fun <TBody, TResult> post(
        url: String,
        body: TBody,
        clazz: Class<TResult>,
        headers: Map<String, String>
    ): TResult? {
        return webClient.mutate()
            .baseUrl(url)
            .build()
            .post()
            .headers { header -> headers.forEach(header::add) }
            .bodyValue(body !!)
            .retrieve()
            .bodyToMono(clazz)
            .block()
    }
}