package com.example.message.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.listener.MessageListener

interface Consumer: AcknowledgingMessageListener<String, Any> {
}