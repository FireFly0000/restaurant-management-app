package com.restaurant.commons.core.dtos;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@SuperBuilder
public class ApiResponse {
    private String message;
    private Object data;
    private Object extra;
    private Date timestamp;
}
