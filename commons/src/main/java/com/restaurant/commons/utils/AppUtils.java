package com.restaurant.commons.utils;

import com.restaurant.commons.core.dtos.ApiResponse;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class AppUtils {

    public static ApiResponse buildResponse(String msg, Object data , Object extra){
        return ApiResponse.builder()
                .message(msg)
                .data(data)
                .extra(extra)
                .timestamp(new Date())
                .build();
    }

    public static <T> T getTaskResult(CompletableFuture<T> task){
        try{
            return (task.isDone() && !task.isCompletedExceptionally()) ? task.get() : null;
        }catch (Exception ex){
            return null;
        }
    }
}
