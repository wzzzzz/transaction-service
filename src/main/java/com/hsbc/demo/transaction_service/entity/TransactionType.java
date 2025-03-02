package com.hsbc.demo.transaction_service.entity;

/**
 * 交易类型: 无效, 转账, 提取, 存款
 */
public enum TransactionType
{
    None,
    Transfer,
    Withdraw,
    Deposit
}
