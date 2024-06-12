package com.example.common.http

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity

interface HttpClient {
    fun <TResult> get(url : String, clazz: Class<TResult>) : ResponseEntity<TResult>?

    fun <TResult> get(url : String, clazz: Class<TResult>, headers : Map<String, String>) : ResponseEntity<TResult>?

    fun <TBody, TResult> post(url: String, body: TBody, clazz: Class<TResult>): ResponseEntity<TResult>?

     fun <TBody, TResult> post(url: String, body: TBody, clazz: Class<TResult>, headers: Map<String, String>): ResponseEntity<TResult>?
     fun <TBody, TResult> post(url: String, body: TBody, clazz: Class<TResult>, onError: (status: HttpStatusCode, body: String) -> Unit): TResult?
     fun <TBody, TResult> post(url: String, body: TBody, clazz: Class<TResult>, headers: Map<String, String>, onError: (status: HttpStatusCode, body: String) -> Unit): TResult?
}