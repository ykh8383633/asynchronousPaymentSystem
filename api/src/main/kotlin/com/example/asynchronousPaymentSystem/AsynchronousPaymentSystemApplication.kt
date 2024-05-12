package com.example.asynchronousPaymentSystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example"])
class AsynchronousPaymentSystemApplication

fun main(args: Array<String>) {
	runApplication<AsynchronousPaymentSystemApplication>(*args)
}
