package com.example.message.kafka.consumer

import com.example.message.kafka.config.properties.MessageProperties
import com.example.message.kafka.consumer.handler.MessageHandler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.support.Acknowledgment
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

    override fun onMessage(data: ConsumerRecord<String, Any>, acknowledgment: Acknowledgment?) {
        val topic = data.topic()
        val value = data.value()
        handlerMap[topic]?.forEach {
            try{
                it.handle(value)
            }
            catch(e: Exception){
                it.onError(e)
            }
            finally {
                acknowledgment?.acknowledge()
            }
        }
    }
}