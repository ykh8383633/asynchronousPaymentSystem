package com.example.common.http

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.lang.Exception
import java.lang.reflect.Type
import java.util.concurrent.Executor

@Component
class HttpClientImpl(
    private val webClient: WebClient
): HttpClient {

    override fun <TResult> get(url: String, clazz: Class<TResult>): ResponseEntity<TResult>? = get(url, clazz, mapOf())

    override fun <TResult> get(url: String, clazz: Class<TResult>, headers: Map<String, String>): ResponseEntity<TResult>? {
        return webClient.mutate()
            .baseUrl(url)
            .build()
            .get()
            .headers { header -> headers.forEach(header::add) }
            .retrieve()
            .toEntity(clazz)
            .block()
    }

    override fun <TBody, TResult> post(url: String, body: TBody, clazz: Class<TResult>): ResponseEntity<TResult>? {
        return post(url, body, clazz, mapOf())
    }

    override fun <TBody, TResult> post(
        url: String,
        body: TBody,
        clazz: Class<TResult>,
        headers: Map<String, String>
    ): ResponseEntity<TResult>? {
        return webClient.mutate()
            .baseUrl(url)
            .build()
            .post()
            .headers { header -> headers.forEach(header::add) }
            .bodyValue(body !!)
            .retrieve()
            .onStatus({it.isError}){response ->
                response.bodyToMono(String::class.java)
                    .map{error ->
                        Exception(error)
                    }
            }
            .toEntity(clazz)
            .block()
    }

    override fun <TBody, TResult> post(
        url: String,
        body: TBody,
        clazz: Class<TResult>,
        onError: (status: HttpStatusCode, body: String) -> Unit
    ): TResult? {
        return post(url, body, clazz, mapOf(), onError)
    }

    override fun <TBody, TResult>post(
        url: String,
        body: TBody,
        clazz: Class<TResult>,
        headers: Map<String, String>,
        onError: (status: HttpStatusCode, body: String) -> Unit
    ): TResult? {
        return webClient.mutate()
            .baseUrl(url)
            .build()
            .post()
            .headers { header -> headers.forEach(header::add) }
            .bodyValue(body!!)
            .retrieve()
            .onStatus({it.isError}){response ->
                response.bodyToMono(String::class.java)
                    .map { onError(response.statusCode(), it) }
                    .subscribe()
                response.createException()
            }
            .bodyToMono(clazz)
            .onErrorResume { Mono.empty() }
            .block()
    }
}