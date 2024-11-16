package com.example.message.kafka.consumer

import com.example.common.executor.SemaphoreThreadPoolTaskExecutor
import com.example.domain.model.message.OrderedMessage
import com.example.message.kafka.config.properties.MessageProperties
import com.example.message.kafka.consumer.handler.MessageHandler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

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
    private val _orderedMessageHandler = OrderedMessageHandler()

    override fun onMessage(data: ConsumerRecord<String, Any>, acknowledgment: Acknowledgment?) {
        val message = data.value()

        if(message is OrderedMessage){
            _orderedMessageHandler.handle(data, acknowledgment)
            return;
        }

        semaphoreTaskExecutor.submit {
            super.onMessage(data, acknowledgment)
        }
    }

    inner class OrderedMessageHandler {
        private val _queue: BlockingQueue<Pair<ConsumerRecord<String, Any>, Acknowledgment?>>
        private val _consumer: Thread
        private val _producer: Executor

        init {
            _queue = LinkedBlockingQueue()
            _producer = Executors.newSingleThreadExecutor();
            _consumer = thread {
                while(true) {
                    val pair = _queue.take()
                    super@ThrottlingConsumer.onMessage(pair.first, pair.second)
                }
            }
        }

        fun handle(record: ConsumerRecord<String, Any>, acknowledgment: Acknowledgment?) {
            _producer.execute{ _queue.add(Pair(record, acknowledgment)) }
        }
    }
}