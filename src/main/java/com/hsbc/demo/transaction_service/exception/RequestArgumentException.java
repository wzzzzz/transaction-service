package com.hsbc.demo.transaction_service.exception;

public class RequestArgumentException extends ServiceBaseException {

    private static final String errorCode = "BAD_REQUEST";

    public RequestArgumentException(String errorMessage) {
        super(errorCode, errorMessage);
    }

    public RequestArgumentException(String errorMessage, Throwable e) {
        super(errorCode, errorMessage, e);
    }
}
