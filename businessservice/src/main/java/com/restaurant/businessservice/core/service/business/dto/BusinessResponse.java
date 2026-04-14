package com.restaurant.businessservice.core.service.business.dto;

import com.restaurant.businessservice.core.service.location.dto.BusinessLocationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private List<BusinessLocationResponse> locations;
    private Date createdAt;
    private Date updatedAt;
    private Long deletedAt;
}
