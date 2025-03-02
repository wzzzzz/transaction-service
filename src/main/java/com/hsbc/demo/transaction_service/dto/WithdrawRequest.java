package com.hsbc.demo.transaction_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
/**
 * 取款请求
 */
public class WithdrawRequest {

    private String userId;
    private BigDecimal amount;
}
