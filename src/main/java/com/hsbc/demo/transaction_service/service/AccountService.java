package com.hsbc.demo.transaction_service.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsbc.demo.transaction_service.dto.AccountDTO;
import com.hsbc.demo.transaction_service.entity.AccountData;
import com.hsbc.demo.transaction_service.entity.AccountStatus;
import com.hsbc.demo.transaction_service.exception.AccountNotFoundException;
import com.hsbc.demo.transaction_service.exception.DuplicateAccountException;
import com.hsbc.demo.transaction_service.repository.AccountRepository;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // 注册新用户
    @Transactional(timeout = 5, rollbackFor = Throwable.class)
    public void RegisterAccount(AccountDTO accountDTO)
    {
        var userId = accountDTO.getUserId();
        var existedAccountOpt = accountRepository.findByUserId(userId);
        if (existedAccountOpt.isPresent())
        {
            throw new DuplicateAccountException();
        }
        var currentTime = new Timestamp(Instant.now().toEpochMilli());
        var accountInfo = AccountData.builder()
            .userId(accountDTO.getUserId())
            .userName(accountDTO.getUserName())
            .firstName(accountDTO.getFirstName())
            .lastName(accountDTO.getLastName())
            .balance(new BigDecimal(0))
            .status(AccountStatus.Active)
            .createTime(currentTime)
            .updateTime(currentTime)
            .build();
        
        accountRepository.save(accountInfo);
    }

    // 查询用户信息
    public AccountDTO GetAccountById(String accountId)
    {
        var accountOpt = accountRepository.findByUserId(accountId);
        if (!accountOpt.isPresent())
        {
            throw new AccountNotFoundException();
        }
        var accountInfo = accountOpt.get();
        if (accountInfo.getStatus() != AccountStatus.Active)
        {
            throw new AccountNotFoundException();
        }

        return AccountDTO.builder()
            .userId(accountInfo.getUserId())
            .userName(accountInfo.getUserName())
            .firstName(accountInfo.getFirstName())
            .lastName(accountInfo.getLastName())
            .balance(accountInfo.getBalance())
            .build();
    }

    public List<AccountDTO> FindAllAccountsByPage(Pageable pageable)
    {
        List<AccountDTO> ret = new ArrayList<>();
        
        var defaultStatusSet = new HashSet<AccountStatus>();
        Collections.addAll(defaultStatusSet, AccountStatus.Active);
        var pageableAccounts = accountRepository.findAllByStatusIn(defaultStatusSet, pageable);
        
        List<AccountData> accountInfoList = pageableAccounts.getContent();
        for (var accountInfo : accountInfoList) {
            ret.add(AccountDTO.builder()
            .userId(accountInfo.getUserId())
            .userName(accountInfo.getUserName())
            .firstName(accountInfo.getFirstName())
            .lastName(accountInfo.getLastName())
            .balance(accountInfo.getBalance())
            .build());
        }
        return ret;
    }
    
    // 只允许更新基本信息，用户名等
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L, multiplier = 2))
    @Transactional(timeout = 2, rollbackFor = Throwable.class)
    public AccountDTO UpdateAccountByUserId(String userId, AccountDTO accountDTO)
    {
        var accountOpt = accountRepository.findByUserId(userId);
        if (!accountOpt.isPresent())
        {
            throw new AccountNotFoundException();
        }

        var accountInfo = accountOpt.get();
        if (StringUtils.isNotEmpty(accountDTO.getUserName()))
        {
            accountInfo.setUserName(accountDTO.getUserName());
        }
        if (StringUtils.isNotEmpty(accountDTO.getFirstName()))
        {
            accountInfo.setFirstName(accountDTO.getFirstName());
        }
        if (StringUtils.isNotEmpty(accountDTO.getLastName()))
        {
            accountInfo.setLastName(accountDTO.getLastName());
        }
        accountInfo.setUpdateTime(new Timestamp(Instant.now().toEpochMilli()));

        var savedData = accountRepository.save(accountInfo);
        return AccountDTO.builder()
            .userId(savedData.getUserId())
            .userName(savedData.getUserName())
            .firstName(savedData.getFirstName())
            .lastName(savedData.getLastName())
            .balance(savedData.getBalance())
            .build();
    }
}
