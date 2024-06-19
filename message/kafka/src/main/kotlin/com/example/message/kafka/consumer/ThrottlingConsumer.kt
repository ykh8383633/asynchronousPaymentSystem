package com.example.message.kafka.consumer

import com.example.common.executor.SemaphoreThreadPoolTaskExecutor
import com.example.message.kafka.config.properties.MessageProperties
import com.example.message.kafka.consumer.handler.MessageHandler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    override fun onMessage(data: ConsumerRecord<String, Any>, acknowledgment: Acknowledgment?) {
        semaphoreTaskExecutor.submit {
            super.onMessage(data, acknowledgment)
            printCurrentTime();
        }
    }

    private fun printCurrentTime() {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        println(Thread.currentThread().name + ": " + now.format(formatter))
    }
}