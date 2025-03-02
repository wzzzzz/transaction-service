package com.hsbc.demo.transaction_service.exception;

public class DuplicateAccountException extends ServiceBaseException {

    private static final String errorCode = "DUPLICATE_ACCOUNT";
    private static final String errorMessage = "user existed!";

    public DuplicateAccountException() {
        super(errorCode, errorMessage);
    }

    public DuplicateAccountException(Throwable e) {
        super(errorCode, errorMessage, e);
    }
}
