package com.example.message.kafka.producer

import com.example.domain.model.message.Message
import com.example.message.kafka.topic.Topic
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class ProducerImpl(
    private val template: KafkaTemplate<String, Any>
): Producer {

    override fun send(topic: Topic, value: Message): CompletableFuture<Boolean> {
        val topicName = topic.name
        return template.send(topicName ,value)
            .exceptionallyAsync { null }
            .thenApplyAsync {it != null}
    }
}