package com.hsbc.demo.transaction_service.dto;

import java.sql.Timestamp;

@Data
@Builder
public class TransferMessage {

    private String sourceUserId;
    private String destUserId;

    private BigDecimal amount;

    private Timestamp timestamp;
}
