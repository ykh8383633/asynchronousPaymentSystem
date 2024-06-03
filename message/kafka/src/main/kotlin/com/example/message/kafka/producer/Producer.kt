package com.example.message.kafka.producer

import com.example.domain.model.message.Message
import com.example.message.kafka.topic.Topic
import java.util.concurrent.CompletableFuture

interface Producer {
    fun send(topic: Topic, value: Message): CompletableFuture<Boolean>
}