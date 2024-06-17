package com.example.message.kafka.consumer

import com.example.common.executor.SemaphoreThreadPoolTaskExecutor
import com.example.message.kafka.config.properties.MessageProperties
import com.example.message.kafka.consumer.handler.MessageHandler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component("ThrottlingConsumer")
@ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
class ThrottlingConsumer(
    @Qualifier("ThrottlingHandlerExecutor") private val semaphoreTaskExecutor: SemaphoreThreadPoolTaskExecutor,
    messageProperties: MessageProperties,
    handlers: MutableList<MessageHandler>
): DefaultConsumer(
    messageProperties,
    handlers
) {

    override fun onMessage(record: ConsumerRecord<String, Any>) {
        semaphoreTaskExecutor.submit {
            super.onMessage(record)
        }
    }
}