package com.example.asynchronousPaymentConsumer.controller

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
    @Qualifier("consumerExecutor") private val consumerThreadPoolTaskExecutor: SemaphoreThreadPoolTaskExecutor
) {

    @PostMapping("/add-permits")
    fun addPermits(@RequestBody request: AddPermitsRequestDto): ResponseEntity<*>{
        val permits = consumerThreadPoolTaskExecutor.addPermits(request.permits);
        return ResponseEntity(AddPermitsResponseDto(permits), HttpStatus.OK)
    }


}