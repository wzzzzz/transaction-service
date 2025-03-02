package com.hsbc.demo.transaction_service.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Hashtable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="transaction")
public class TransactionData {

    // 唯一标识Id
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private String transactionId;
    
    // 来源于交易发起方用于查找对应的交易&去重, 主要用于三方发起的购买
    private String sourceTradeId;

    private BigDecimal amount;

    private TransactionType type;
    private TransactionStatus status;

    private String sourceAccountId;
    private String destAccountId;

    private Timestamp createTime;
    private Timestamp updateTime;
    
    // 补充信息
    private Hashtable<String, String> AddionalData;
}