package com.example.message.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.listener.MessageListener

interface Consumer: MessageListener<String, Any> {
}