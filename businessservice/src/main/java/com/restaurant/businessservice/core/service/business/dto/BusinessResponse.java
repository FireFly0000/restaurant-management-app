package com.restaurant.businessservice.core.service.business.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public class BusinessResponse {
    private String id;
    private String name;
    private String businessType;
    private String description;
    private String avatarUrl;
    private String coverImgUrl;
    private String phoneNumber;
    private String email;
    private String websiteUrl;
    private Boolean isActive;
    private Integer locationCount;
    private Date createdAt;
    private Date updatedAt;
}
