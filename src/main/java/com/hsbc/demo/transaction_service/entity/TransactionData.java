package com.hsbc.demo.transaction_service.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class TransactionData {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    public String Id;
    
    public String TradeId;

    public BigDecimal Amount;

    public String SourceAccountId;
    
    public String DestAccountId;

    public Hashtable<String, String> AddionalData;
}