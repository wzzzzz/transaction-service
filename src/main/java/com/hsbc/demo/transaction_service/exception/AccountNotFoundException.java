package com.hsbc.demo.transaction_service.exception;

public class AccountNotFoundException extends ServiceBaseException {

    private static final String errorCode = "ACCOUNT_NOT_FOUND";
    private static final String errorMessage = "User Not Found";

    public AccountNotFoundException() {
        super(errorCode, errorMessage);
    }

    public AccountNotFoundException(Throwable e) {
        super(errorCode, errorMessage, e);
    }
}
