package com.restaurant.businessservice.core.service.business.dto;

import com.restaurant.businessservice.core.service.location.dto.BusinessLocationResponse;
import lombok.Builder;

import java.util.Date;
import java.util.Set;

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
    private Integer locationsCount;
    private Integer ordersCount;
    private Double averageRating;
    private Set<BusinessLocationResponse> locations;
    private Date createdAt;
    private Date updatedAt;
}
