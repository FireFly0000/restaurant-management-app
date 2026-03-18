package com.restaurant.commons.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseFilter {
    private Integer page;
    private Integer limit;
    private String sort;
    private String sortBy;
    private String search;
}
