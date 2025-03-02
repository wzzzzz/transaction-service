package com.hsbc.demo.transaction_service.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hsbc.demo.transaction_service.entity.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class TransactionDTO {

    private String transactionId;
    private String sourceTradeId;

    private BigDecimal amount;

    private TransactionType type;

    private String sourceAccountId;
    private String sourceAccountName;

    private String destAccountId;
    private String destAccountName;

    private TransactionStatus status;

    private Timestamp createTime;
}
