package com.hsbc.demo.transaction_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import com.hsbc.demo.transaction_service.dto.*;
import com.hsbc.demo.transaction_service.exception.RequestArgumentException;
import com.hsbc.demo.transaction_service.exception.ServiceBaseException;
import com.hsbc.demo.transaction_service.messaging.TransactionMessageSender;
import com.hsbc.demo.transaction_service.service.TransactionService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class TransactionController {

    @Resource
    private TransactionService transactionService;

    @Resource
    private TransactionMessageSender messageSender;
    
    @Resource
    private Environment environment;

    @GetMapping("/transaction/list")
    public List<TransactionDTO> findAllAccounts(@RequestParam(value = "PageNo", required = false, defaultValue = "1")Integer pageNumber, 
                                            @RequestParam(value = "Size", required = false, defaultValue = "20")Integer pageSize) {
        try {
            // 默认按 create_time 排序
            Pageable sortedByTime= PageRequest.of(0, 3, Sort.by("createTime").descending());
            var transactionList = transactionService.FindAllTransactionsByPage(sortedByTime);
            
            return transactionList;
        } catch (Exception e) {
            log.error("查询失败", e);
            throw new ServiceBaseException("查询失败:" + e.getMessage(), e);
        }
    }

    @GetMapping("/transaction/list/{userId}")
    public List<TransactionDTO> findAccountById(@PathVariable String userId,
                                                @RequestParam(value = "PageNo", required = false, defaultValue = "1")Integer pageNumber, 
                                                @RequestParam(value = "Size", required = false, defaultValue = "20")Integer pageSize) {
        try {
            // 查询既是source_id 又是 dest_id 的交易
            Pageable sortedByTime= PageRequest.of(0, 3, Sort.by("createTime").descending());
            var transactionList = transactionService.FindAllTransactionsByUser(userId, sortedByTime);
            return transactionList;
        } catch (Exception e) {
            log.error("查询失败", e);
            throw new ServiceBaseException("查询失败:" + e.getMessage(), e);
        }
    }

    @PostMapping("/transaction/deposit")
    public TransactionDTO depositMoney(@RequestBody DepositRequest request) {

        log.info("[depositMoney]Request: {}", JSON.toJSONString(request));
        if (StringUtils.isEmpty(request.getUserId()))
        {
            throw new RequestArgumentException("user id is empty");
        }
        if (request.getAmount().compareTo(new BigDecimal(0)) <= 0)
        {
            throw new RequestArgumentException("amount must be greater than 0");
        }

        var transaction = transactionService.MakeDepoit(request.getUserId(), request.getAmount());
        return transaction;
    }

    @PostMapping("/transaction/withdraw")
    public TransactionDTO withdrawMoney(@RequestBody WithdrawRequest request) {
        
        log.info("[withdrawMoney]Request: {}", JSON.toJSONString(request));
        if (StringUtils.isEmpty(request.getUserId()))
        {
            throw new RequestArgumentException("user id is empty");
        }
        if (request.getAmount().compareTo(new BigDecimal(0)) <= 0)
        {
            throw new RequestArgumentException("amount must be greater than 0");
        }

        var transaction = transactionService.MakeWithdraw(request.getUserId(), request.getAmount());
        return transaction;
    }

    @PostMapping("/transaction/transfer")
    public TransactionDTO transferMoney(@RequestBody TransferRequest request) {
        
        log.info("[transferMoney]Request: {}", JSON.toJSONString(request));
        if (StringUtils.isEmpty(request.getSourceUserId()))
        {
            throw new RequestArgumentException("source user id is empty");
        }
        if (StringUtils.isEmpty(request.getDestUserId()))
        {
            throw new RequestArgumentException("dest user id is empty");
        }
        if (request.getAmount().compareTo(new BigDecimal(0)) <= 0)
        {
            throw new RequestArgumentException("amount must be greater than 0");
        }
        
        var transaction = transactionService.MakeTransfer(request.getSourceUserId(),request.getDestUserId(), request.getAmount());
        return transaction;
    }
}
