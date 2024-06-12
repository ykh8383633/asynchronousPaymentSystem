package com.example.asynchronousPaymentSystem.service

import com.example.domain.dto.OrderRequestDto
import com.example.domain.enums.OrderStatus
import com.example.domain.model.message.RequestOrderMessage
import com.example.domain.model.order.Order
import com.example.mysql.repository.order.OrderWriter
import com.example.message.kafka.producer.Producer
import com.example.message.kafka.topic.RequestOrder
import com.example.mysql.repository.orderimport.OrderReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderReader: OrderReader,
    private val orderWriter: OrderWriter,
    private val productService: ProductService,
    private val producer: Producer,
    private val requestOrder: RequestOrder
) {
    @Transactional(value = "orderTransactionManager")
    fun order(orderRequestDto: OrderRequestDto): Order {
        val price = this.calcPrice(orderRequestDto)

        if(price != orderRequestDto.amount){
            //throw Exception("AMOUNT IS NOT CORRECT")
        }

        val order = Order(
            productId = orderRequestDto.productId,
            userId = orderRequestDto.userId,
            quantity = orderRequestDto.quantity,
            price = price,
            status = OrderStatus.REQUEST
        )

        val saved = orderWriter.save(order)

        val message = RequestOrderMessage(
            orderId = saved.id !!,
            quantity = saved.quantity,
            productId = saved.productId,
            userId = saved.userId,
            paymentId = orderRequestDto.paymentId,
            amount = orderRequestDto.amount,
            paymentOrderId = orderRequestDto.pgOrderId
        )

        producer.send(requestOrder, message)

        return saved
    }

    private fun calcPrice(orderRequest: OrderRequestDto): Long{
        val product = productService.findById(orderRequest.productId) ?: throw Exception("PRODUCT NOT FOUND");
        return product.price * orderRequest.quantity
    }

    fun findAll(): MutableList<Order> = orderReader.findAll()
}