package com.hsbc.demo.transaction_service.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class AccountDTO {

    private String userId;
    private String userName;

    private BigDecimal balance;

    private String firstName;
    private String lastName;
}
