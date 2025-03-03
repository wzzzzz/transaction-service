package com.hsbc.demo.transaction_service.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferMessage {

    private String sourceUserId;
    private String destUserId;

    private BigDecimal amount;

    private Timestamp timestamp;
}
