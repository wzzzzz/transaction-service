package com.hsbc.demo.transaction_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
/**
 * 存款请求
 */
public class DepositRequest {
    private String userId;
    private BigDecimal amount;
}
