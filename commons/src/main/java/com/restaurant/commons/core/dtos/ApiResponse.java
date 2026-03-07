package com.restaurant.commons.core.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private Object data;
    private Object extra;
    private Date timestamp;
}
