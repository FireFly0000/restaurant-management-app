package com.restaurant.businessservice.core.service.location.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public class BusinessLocationResponse {
    private String id;
    private String businessId;
    private String locationName;
    private String branchNumber;
    private String address;
    private String phoneNumber;
    private String email;
    private String managerId;
    private String managerName;
    private Boolean isActive;
    private Date startDate;
    private Date createdAt;
    private Date updatedAt;
}
