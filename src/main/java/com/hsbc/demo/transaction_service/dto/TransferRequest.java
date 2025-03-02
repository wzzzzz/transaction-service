package com.hsbc.demo.transaction_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
/**
 * 转账请求
 */
public class TransferRequest {
    
    private String sourceUserId;
    private String destUserId;

    private BigDecimal amount;
}
