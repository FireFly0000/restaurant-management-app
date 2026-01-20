package com.restaurant.commons.core.dtos;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ApiResponse {
    private String message;
    private Object data;
    private Object extra;
}
