package com.restaurant.businessservice.core.service.location.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BusinessLocationResponse {
    private String id;
    private String businessId;
    private String locationName;
    private String branchNumber;
    private String address;
    private String phoneNumber;
    private String email;
    private String managerId;
    private String managerFirstName;
    private String managerLastName;
    private Boolean isActive;
    private LocalDateTime startDate;
    private Date createdAt;
    private Date updatedAt;
}
