package com.hsbc.demo.transaction_service.config;

@Configuration
public class RabbitMQConfig {

    private static final String TRANSFER_QUQUE = "transfer-queue";

    @Bean
    public Queue TransferQueue() {
        return new Queue(TRANSFER_QUQUE, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
