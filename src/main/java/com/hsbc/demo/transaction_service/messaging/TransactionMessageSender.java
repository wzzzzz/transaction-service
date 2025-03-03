package com.hsbc.demo.transaction_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsbc.demo.transaction_service.config.RabbitMQConfig;
import com.hsbc.demo.transaction_service.dto.TransferMessage;

@Service
public class TransactionMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendTransferMessage(TransferMessage message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.TRANSFER_QUQUE, message);
    }
}
