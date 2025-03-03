package com.hsbc.demo.transaction_service.dto;

import com.alibaba.fastjson.JSON;

public record ServiceResponse<T>(String errCode, String errMessage, T data) {

    // 成功
    public static <T> ServiceResponse<T> success(T data) {
        return new ServiceResponse<>("0", "Success", data);
    }

    // 失败
    public static <T> ServiceResponse<T> buildFailure(String errCode, String errMessage) {
        return new ServiceResponse<>(errCode, errMessage, null);
    }

    @Override
    public String toString() {
        return "ServiceResponse [errCode=" + errCode + ", errMessage=" + errMessage + ", data=" + JSON.toJSONString(data) + "]";
    }
}