package com.restaurant.commons.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseFilter {
    private final Integer defaultLimit = 30;

    private Integer page;
    private Integer limit;
    private String sort;
    private String sortBy;
    private String search;

    public void setLimit(Integer limit) {
        if(limit == null){
            this.limit = defaultLimit;
        }else if(limit < 1){
            this.limit = defaultLimit;
        }else if(limit > 50){
            this.limit = 50;
        }else{
            this.limit = limit;
        }
    }
}
