package com.restaurant.commons.utils;

import com.restaurant.commons.core.dtos.ApiResponse;

public class AppUtils {
    public static ApiResponse buildResponse(String message, Object data, Object extra){
        return ApiResponse.builder()
                .message(message)
                .data(data)
                .extra(extra)
                .build();
    }
}
