package com.restaurant.authservice.core.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {
    private String id;
    private String email;
    private String lastName;
    private String firstName;
}
