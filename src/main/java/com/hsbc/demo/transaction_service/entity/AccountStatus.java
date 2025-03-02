package com.hsbc.demo.transaction_service.entity;

/**
 * 用户状态: 无效，有效已激活, 封禁, 删除注销
 */
public enum AccountStatus {
    None,
    Active,
    Blocked,
    Deleted
}
