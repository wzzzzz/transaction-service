package com.hsbc.demo.transaction_service.messaging;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsbc.demo.transaction_service.config.RabbitMQConfig;
import com.hsbc.demo.transaction_service.dto.*;
import com.hsbc.demo.transaction_service.entity.*;
import com.hsbc.demo.transaction_service.exception.*;
import com.hsbc.demo.transaction_service.repository.*;

@Service
public class TransactionMessageConsumer {

    private static final int MAX_RETRIES = 3;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RabbitListener(queues = RabbitMQConfig.TRANSFER_QUQUE)
    public void receiveMessage(TransferMessage message) {

        System.out.println("Received message: " + message);

        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                log.info("Message processed successfully: " + message);
                MakeTransfer(message.getSourceUserId(), message.getDestUserId(), message.getAmount(), message.getTimestamp());
                break;
            } catch (Exception e) {
                retryCount++;
                log.warn("Retry attempt " + retryCount + " for message: " + message);
                if (retryCount == MAX_RETRIES) {
                    log.info("Max retries reached, sending to DLX: " + message);
                    // To do: 将消息发送到Dead letter
                    throw e;
                }
            }
        }
    }

    @Transactional(timeout = 5, rollbackFor = Throwable.class)
    private void MakeTransfer(String sourceUserId, String destUserId, BigDecimal amount, Timestamp transactionTime)
    {
        var userSet = new HashSet<String>();
        userSet.add(sourceUserId);
        userSet.add(destUserId);
        var userList = accountRepository.findAllByUserIdIn(userSet);

        var sourceUserOpt = userList.stream().filter(u -> sourceUserId.equals(u.getUserId())).findFirst();
        var destUserOpt = userList.stream().filter(u -> destUserId.equals(u.getUserId())).findFirst();
        if (!sourceUserOpt.isPresent() || !destUserOpt.isPresent())
        {
            throw new AccountNotFoundException();
        }

        var sourceUser = sourceUserOpt.get();
        var destUser = destUserOpt.get();
        if (sourceUser.getBalance().compareTo(amount) < 0)
        {
            throw new InsufficientBalanceException();
        }

        // 转账增加余额
        sourceUser.setBalance(sourceUser.getBalance().add(amount.negate()));
        sourceUser.setUpdateTime(transactionTime);
        destUser.setBalance(destUser.getBalance().add(amount));
        destUser.setUpdateTime(transactionTime);

        // 交易记录
        var transactionRecord = TransactionData.builder()
            .sourceAccountId(sourceUser.getUserId())
            .destAccountId(destUser.getUserId())
            .type(TransactionType.Transfer)
            .status(TransactionStatus.Complete)
            .amount(amount)
            .createTime(transactionTime)
            .updateTime(transactionTime)
            .build();

        accountRepository.save(sourceUser);
        accountRepository.save(destUser);
        transactionRepository.save(transactionRecord);
    }
}
