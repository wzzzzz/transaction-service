package com.hsbc.demo.transaction_service.entity;

/**
 * 交易状态: 无效, 处理中, 成功, 失败, 删除
 */
public enum TransactionStatus {
    None,
    Pending,
    Complete,
    Failed,
    Deleted
}
