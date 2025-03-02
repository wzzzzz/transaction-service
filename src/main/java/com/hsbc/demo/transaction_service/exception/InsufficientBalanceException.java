package com.hsbc.demo.transaction_service.exception;

public class InsufficientBalanceException extends ServiceBaseException {

    private static final String errorCode = "INSUFFICIENT_BALANCE";
    private static final String errorMessage = "insufficient balance!";

    public InsufficientBalanceException() {
        super(errorCode, errorMessage);
    }

    public InsufficientBalanceException(Throwable e) {
        super(errorCode, errorMessage, e);
    }
}
