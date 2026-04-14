package com.restaurant.authservice.core.service.auth.dto;

import com.restaurant.commons.constant.ContactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyAccountResponse {
    private ContactType type;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean verified;
    private boolean isTokenExpired;
}
