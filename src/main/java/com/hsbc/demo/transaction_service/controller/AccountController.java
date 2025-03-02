package com.hsbc.demo.transaction_service.controller;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.hsbc.demo.transaction_service.dto.AccountDTO;
import com.hsbc.demo.transaction_service.exception.RequestArgumentException;
import com.hsbc.demo.transaction_service.exception.ServiceBaseException;
import com.hsbc.demo.transaction_service.service.AccountService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class AccountController {

    @Resource
    private AccountService accountService;

    @GetMapping("/account")
    public List<AccountDTO> findAllAccounts(@RequestParam(value = "PageNo", required = false, defaultValue = "1")Integer pageNumber, 
                                            @RequestParam(value = "Size", required = false, defaultValue = "20")Integer pageSize) {
        try {
            // 默认按user name 排序
            Pageable sortedByName = PageRequest.of(0, 3, Sort.by("UserName").ascending());
            var accountDTOList = accountService.FindAllAccountsByPage(sortedByName);
            
            return accountDTOList;
        } catch (Exception e) {
            log.error("查询失败", e);
            throw new ServiceBaseException("查询失败:" + e.getMessage(), e);
        }
    }

    @GetMapping("/account/{userId}")
    public AccountDTO findAccountById(@PathVariable String userId) {
        var accountInfo = accountService.GetAccountById(userId);
        return accountInfo;
    }

    @PutMapping("/account/{userId}")
    public AccountDTO updateAccountById(@PathVariable(name = "userId") String userId, @RequestBody AccountDTO accountInfo) {
        var updatedData = accountService.UpdateAccountByUserId(userId, accountInfo);
        return updatedData;
    }

    @PostMapping("/account/register")
    public AccountDTO createAccount(@RequestBody AccountDTO accountInfo) {
        log.info("[createAccount]Request: {}", JSON.toJSONString(accountInfo));

        if (StringUtils.isEmpty(accountInfo.getUserName()))
        {
            throw new RequestArgumentException("user name is empty");
        }
        
        // 生成一个外显的通用Id
        if (StringUtils.isEmpty(accountInfo.getUserId()))
        {
            var uuId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            accountInfo.setUserId("UID" + uuId.substring(0,27 ));
        }
        accountService.RegisterAccount(accountInfo);
        return accountInfo;
    }
}
