package com.hsbc.demo.transaction_service.entity;

import java.math.BigDecimal;
import java.util.ArrayList;

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
public class AccountData
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public String Id;

    public BigDecimal Balance;

    public String FirstName;
    public String LastName;

    public ArrayList<String> Address;
}