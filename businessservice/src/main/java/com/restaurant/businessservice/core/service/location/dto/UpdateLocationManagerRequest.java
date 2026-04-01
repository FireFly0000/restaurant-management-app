package com.restaurant.businessservice.core.service.location.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateLocationManagerRequest {
    @NotBlank(message = "{location.manager_id.not-blank}")
    private String managerId;
    @NotBlank(message = "{location.business_id.not-blank}")
    private String businessId;
}
