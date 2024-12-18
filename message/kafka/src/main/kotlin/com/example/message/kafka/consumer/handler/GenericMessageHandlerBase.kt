package com.example.message.kafka.consumer.handler

import com.example.domain.model.message.Message

abstract class GenericMessageHandlerBase<TMessage: Message>: MessageHandler {
    override fun handle(data: Any) {
        val typedData = mapRequest(data)
        handleMessage(typedData)
    }

    protected open fun mapRequest(data: Any): TMessage {
        return data as? TMessage ?: throw Exception("INVALID MESSAGE TYPE")
    }

    abstract  fun handleMessage(data: TMessage)
}