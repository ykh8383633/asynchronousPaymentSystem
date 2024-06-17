package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.AddPermitsRequestDto
import com.example.domain.model.message.ThrottlingConsumerMessage
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.ThrottlingConsumerTopic
import org.springframework.stereotype.Service

@Service
class ConsumerConfigureService(
    private val producer: Producer,
    private val throttlingConsumerTopic: ThrottlingConsumerTopic
) {

    fun throttleConsumer(request: AddPermitsRequestDto){
        producer.send(throttlingConsumerTopic, ThrottlingConsumerMessage(request.permits))
    }
}