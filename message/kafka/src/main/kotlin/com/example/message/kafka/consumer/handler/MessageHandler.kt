package com.example.message.kafka.consumer.handler

import com.example.message.kafka.topic.Topic

interface MessageHandler {
    val topic: Topic
    fun handle(data: Any)

    fun onError(err: Exception)
}