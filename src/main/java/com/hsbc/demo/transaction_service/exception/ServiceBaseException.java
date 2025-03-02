package com.hsbc.demo.transaction_service.exception;


public class ServiceBaseException extends RuntimeException {

    private static final long serialVersionUID = 10001L;

    private String errorCode = "INTERNAL_ERROR";

    public ServiceBaseException(String errMessage) {
        super(errMessage);
    }

    public ServiceBaseException(String errorCode, String errMessage) {
        super(errMessage);
        this.errorCode = errorCode;
    }

    public ServiceBaseException(String errMessage, Throwable e) {
        super(errMessage, e);
    }

    public ServiceBaseException(String errorCode, String errMessage, Throwable e) {
        super(errMessage, e);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}