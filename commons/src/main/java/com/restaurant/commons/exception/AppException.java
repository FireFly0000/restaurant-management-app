package com.restaurant.commons.exception;

import lombok.Getter;

@Getter
public class AppException extends AbstractBaseException {
    private final String errorCode;
    private final String httpStatus;
    private final Object data;

    public AppException(String msg, String errorCode, String httpStatus) {
        super(msg);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.data = null;
    }

    public AppException(String msg, String errorCode, String httpStatus, Object data) {
        super(msg);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.data = data;
    }

    public AppException(String msg, String errorCode, String httpStatus, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.data = null;
    }
}
