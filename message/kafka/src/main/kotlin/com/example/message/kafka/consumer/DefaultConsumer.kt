package com.example.message.kafka.consumer

import com.example.message.kafka.config.properties.MessageProperties
import com.example.message.kafka.consumer.handler.MessageHandler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component("DefaultConsumer")
@ConditionalOnProperty(prefix = "spring.kafka", name = ["isConsumer"])
class DefaultConsumer(
    private val messageProperties: MessageProperties,
    private val handlers: MutableList<MessageHandler>
): Consumer {
    private val handlerMap: MutableMap<String, MutableList<MessageHandler>> = mutableMapOf()

    init {
        handlers.forEach{
            val handlers = handlerMap.getOrPut(it.topic.name){ mutableListOf() }
            handlers.add(it)
        }
    }

    override fun onMessage(record: ConsumerRecord<String, Any>) {
        val topic = record.topic()
        val value = record.value()
        val handlers = handlerMap[topic]

        handlers?.forEach { it.handle(value) }
    }
}