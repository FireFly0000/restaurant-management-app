package com.restaurant.commons.exception;

import lombok.Getter;

@Getter
public class AppException extends AbstractBaseException {
    private final String errorCode;
    private final String httpStatus;
    public AppException(String msg, String errorCode, String httpStatus) {
        super(msg);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AppException(String msg, String errorCode, String httpStatus, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
