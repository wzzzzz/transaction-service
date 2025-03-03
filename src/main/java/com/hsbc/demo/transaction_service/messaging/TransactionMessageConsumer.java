package com.hsbc.demo.transaction_service.messaging;

import java.sql.Timestamp;

import com.hsbc.demo.transaction_service.config.RabbitMQConfig;
import com.hsbc.demo.transaction_service.dto.TransferMessage;

@Service
public class TransactionMessageConsumer {

    private static final int MAX_RETRIES = 3;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RabbitListener(queues = rabbitMQConfig.TransferQueue())
    public void receiveMessage(TransferMessage message) {

        System.out.println("Received message: " + message);

        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                // 模拟业务逻辑失败
                if (message.getContent().contains("error")) {
                    throw new RuntimeException("Message processing failed!");
                }
                // 正常处理逻辑
                System.out.println("Message processed successfully: " + message);
                break; // 处理成功，退出循环
            } catch (Exception e) {
                retryCount++;
                System.out.println("Retry attempt " + retryCount + " for message: " + message);
                if (retryCount == MAX_RETRIES) {
                    System.out.println("Max retries reached, sending to DLX: " + message);
                    // 将消息发送到死信队列
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
