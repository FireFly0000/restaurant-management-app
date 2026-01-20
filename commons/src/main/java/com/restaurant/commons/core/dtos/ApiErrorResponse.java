package com.restaurant.commons.core.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ApiErrorResponse extends ApiResponse {
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("http_status")
    private String httpStatus;
}
