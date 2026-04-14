package com.restaurant.authservice.core.service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "{auth.signin.email.required}")
    @Email(message = "{auth.signin.email.invalid}")
    private String email;

    @NotBlank(message = "{auth.signin.password.required}")
    private String password;
}