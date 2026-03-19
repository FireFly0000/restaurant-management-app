package com.restaurant.businessservice.core.service.business.dto;

import com.restaurant.commons.core.BaseFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class BusinessFilter extends BaseFilter {
    private Boolean isActived;
    private Boolean isDeleted;
    private String type;
}
