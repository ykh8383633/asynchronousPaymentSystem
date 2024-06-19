package com.example.asynchronousPaymentConsumer.handler

import com.example.common.executor.SemaphoreThreadPoolTaskExecutor
import com.example.domain.model.message.RejectOrderMessage
import com.example.domain.model.message.ThrottlingConsumerMessage
import com.example.message.kafka.consumer.handler.GenericMessageHandlerBase
import com.example.message.kafka.topic.ThrottlingConsumerTopic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class ThrottlingConsumerHandler(
    override val topic: ThrottlingConsumerTopic,
    @Qualifier("ThrottlingHandlerExecutor") private val throttleExecutor: SemaphoreThreadPoolTaskExecutor
): GenericMessageHandlerBase<ThrottlingConsumerMessage>() {

    override fun handleMessage(data: ThrottlingConsumerMessage) {
        val permits = throttleExecutor.addPermits(data.permits)
        println("currentPermits: $permits")
    }

    override fun onError(err: Exception) {
    }
}