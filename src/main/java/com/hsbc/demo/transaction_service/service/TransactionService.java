package com.hsbc.demo.transaction_service.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsbc.demo.transaction_service.dto.TransactionDTO;
import com.hsbc.demo.transaction_service.entity.AccountStatus;
import com.hsbc.demo.transaction_service.entity.TransactionData;
import com.hsbc.demo.transaction_service.entity.TransactionStatus;
import com.hsbc.demo.transaction_service.entity.TransactionType;
import com.hsbc.demo.transaction_service.exception.AccountNotFoundException;
import com.hsbc.demo.transaction_service.exception.InsufficientBalanceException;
import com.hsbc.demo.transaction_service.repository.AccountRepository;
import com.hsbc.demo.transaction_service.repository.TransactionRepository;

import jakarta.el.MethodNotFoundException;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * 分页查询所有交易
     * @param pageable 分页参数
     * @return
     */
    public List<TransactionDTO> FindAllTransactionsByPage(Pageable pageable)
    {
        List<TransactionDTO> ret = new ArrayList<>();
        
        var pageableTransactions = transactionRepository.findAll(pageable);

        for (var transactionData : pageableTransactions.getContent()) {
            var transaction = convertTransactionDTO(transactionData);
            if (transaction != null)
            {
                ret.add(transaction);
            }
        }
        return ret;
    }

    /**
     * 分页查询当前用户所有产生的交易
     * @param pageable 分页参数
     * @return
     */
    public List<TransactionDTO> FindAllTransactionsByUser(String userId, Pageable pageable)
    {
        List<TransactionDTO> ret = new ArrayList<>();
        
        var pageableTransactions = transactionRepository.findAllBySourceAccountIdOrDestAccountId(userId, userId, pageable);

        for (var transactionData : pageableTransactions.getContent()) {
            var transaction = convertTransactionDTO(transactionData);
            if (transaction != null)
            {
                ret.add(transaction);
            }
        }
        return ret;
    }

    /**
     * 用户存款
     * @param userId 用户Id
     * @param amount 存款金额
     * @return 交易详情
     */
    @Transactional(timeout = 5, rollbackFor = Throwable.class)
    public TransactionDTO MakeDepoit(String userId, BigDecimal amount)
    {
        var userInfoOpt = accountRepository.findByUserId(userId);
        if (!userInfoOpt.isPresent())
        {
            throw new AccountNotFoundException();
        }

        var userInfo = userInfoOpt.get();
        if (userInfo.getStatus() != AccountStatus.Active)
        {
            throw new AccountNotFoundException();
        }

        var curTime = new Timestamp(Instant.now().toEpochMilli());
        
        userInfo.setBalance(userInfo.getBalance().add(amount));
        userInfo.setUpdateTime(curTime);

        var transactionRecord = TransactionData.builder()
            .sourceAccountId(userId)
            .type(TransactionType.Deposit)
            .status(TransactionStatus.Complete)
            .amount(amount)
            .createTime(curTime)
            .updateTime(curTime)
            .build();
        
        userInfo = accountRepository.save(userInfo);
        transactionRecord = transactionRepository.save(transactionRecord);

        var ret = convertTransactionDTO(transactionRecord);
        ret.setSourceAccountName(userInfo.getUserName());
        return ret;
    }

    /**
     * 用户提取
     * @param userId 用户Id
     * @param amount 金额
     * @return 交易详情
     */
    @Transactional(timeout = 5, rollbackFor = Throwable.class)
    public TransactionDTO MakeWithdraw(String userId, BigDecimal amount)
    {
        var userInfoOpt = accountRepository.findByUserId(userId);
        if (!userInfoOpt.isPresent())
        {
            throw new AccountNotFoundException();
        }
        var userInfo = userInfoOpt.get();
        if (userInfo.getStatus() != AccountStatus.Active)
        {
            throw new AccountNotFoundException();
        }
        
        var currentBalance = userInfo.getBalance();
        if (currentBalance.compareTo(amount) < 0)
        {
            throw new InsufficientBalanceException();
        }

        var curTime = new Timestamp(Instant.now().toEpochMilli());

        userInfo.setBalance(currentBalance.add(amount.negate()));
        userInfo.setUpdateTime(curTime);

        var transactionRecord = TransactionData.builder()
            .sourceAccountId(userId)
            .type(TransactionType.Withdraw)
            .status(TransactionStatus.Complete)
            .amount(amount)
            .createTime(curTime)
            .updateTime(curTime)
            .build();
        
        userInfo = accountRepository.save(userInfo);
        transactionRecord = transactionRepository.save(transactionRecord);

        var ret = convertTransactionDTO(transactionRecord);
        ret.setSourceAccountName(userInfo.getUserName());
        return ret;
    }

    /**
     * 内部账户之间的转账
     * @param sourceUserId 转账发起方
     * @param destUserId 转账接收方
     * @param amount 转账金额
     * @return 交易详情
     */
    @Transactional(timeout = 5, rollbackFor = Throwable.class)
    public TransactionDTO MakeTransfer(String sourceUserId, String destUserId, BigDecimal amount)
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

        var curTime = new Timestamp(Instant.now().toEpochMilli());

        // 转账增加余额
        sourceUser.setBalance(sourceUser.getBalance().add(amount.negate()));
        sourceUser.setUpdateTime(curTime);
        destUser.setBalance(destUser.getBalance().add(amount));
        destUser.setUpdateTime(curTime);

        // 交易记录
        var transactionRecord = TransactionData.builder()
            .sourceAccountId(sourceUser.getUserId())
            .destAccountId(destUser.getUserId())
            .type(TransactionType.Transfer)
            .status(TransactionStatus.Complete)
            .amount(amount)
            .createTime(curTime)
            .updateTime(curTime)
            .build();

        sourceUser = accountRepository.save(sourceUser);
        destUser = accountRepository.save(destUser);
        transactionRecord = transactionRepository.save(transactionRecord);
        
        var ret = convertTransactionDTO(transactionRecord);
        ret.setSourceAccountName(sourceUser.getUserName());
        ret.setDestAccountName(destUser.getUserName());
        return ret;
    }

    /**
     * TO DO: 实现购买方法 
     */
    public TransactionDTO MakePurchase(String userId, BigDecimal amount, String referenceTradeId)
    {
        throw new MethodNotFoundException();
    }

    private TransactionDTO convertTransactionDTO(TransactionData transactionData)
    {
        if (transactionData == null)
        {
            return null;
        }
        return TransactionDTO.builder()
        .transactionId(transactionData.getTransactionId())
        .type(transactionData.getType())
        .status(transactionData.getStatus())
        .amount(transactionData.getAmount())
        .createTime(transactionData.getCreateTime())
        .sourceAccountId(transactionData.getSourceAccountId())
        .destAccountId(transactionData.getDestAccountId())
        .build();
    }
}
