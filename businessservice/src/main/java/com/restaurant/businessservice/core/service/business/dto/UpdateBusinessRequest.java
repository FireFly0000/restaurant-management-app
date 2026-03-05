package com.restaurant.businessservice.core.service.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBusinessRequest {
    private String name;
    private String businessType;
    private String description;
    private String email;
    private String phoneNumber;
    private String websiteUrl;
}
