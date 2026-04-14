package com.restaurant.storageservice.exception;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.dtos.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final Logger _log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ApiErrorResponse exceptionHandler(Exception ex){
        _log.error(ex.getMessage(), ex.getCause());
        return ApiErrorResponse.builder()
                .message("Unexcepted Error.")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .errorCode(Constant.RES0001)
                .build();
    }
}