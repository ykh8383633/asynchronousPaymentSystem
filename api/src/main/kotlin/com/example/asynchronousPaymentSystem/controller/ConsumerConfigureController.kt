package com.example.asynchronousPaymentSystem.controller

import com.example.asynchronousPaymentSystem.service.ConsumerConfigureService
import com.example.common.executor.SemaphoreThreadPoolTaskExecutor
import com.example.domain.dto.AddPermitsRequestDto
import com.example.domain.dto.AddPermitsResponseDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/v1/consumer/configure")
class ConsumerConfigureController(
    private val consumerConfigureService: ConsumerConfigureService
) {
    @PostMapping("/add-permits")
    fun addPermits(@RequestBody request: AddPermitsRequestDto): ResponseEntity<Unit> {
        consumerConfigureService.throttleConsumer(request)
        return ResponseEntity.noContent().build()
    }
}