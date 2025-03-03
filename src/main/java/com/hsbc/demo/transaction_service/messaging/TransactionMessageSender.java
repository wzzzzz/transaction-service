package com.hsbc.demo.transaction_service.messaging;

import com.hsbc.demo.transaction_service.config.RabbitMQConfig;
import com.hsbc.demo.transaction_service.dto.TransferMessage;

@Service
public class TransactionMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    public void sendTransferMessage(TransferMessage message) {
        rabbitTemplate.convertAndSend(rabbitMQConfig.TransferQueue(), message);
    }
}
