package com.example.asynchronousPaymentConsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example"])
class AsynchronousPaymentConsumer

fun main(args: Array<String>) {
    runApplication<AsynchronousPaymentConsumer>(*args)
}
