package com.restaurant.authservice.core.service.auth.dto;

import com.restaurant.authservice.core.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatch(password = "password", confirmPassword = "confirmPassword")
public class RegisterRequest {

    @NotBlank(message = "{auth.signup.email.required}")
    @Email(message = "{auth.signup.email.invalid}")
    private String email;

    @NotBlank(message = "{auth.signup.password.required}")
    @Size(min = 8, max = 100, message = "{auth.signup.password.size}")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "{auth.signup.password.pattern}"
    )
    private String password;

    @NotBlank(message = "{auth.signup.confirmPassword.required}")
    private String confirmPassword;

    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "{auth.signup.phone.invalid}"
    )
    private String phoneNumber;

    @NotBlank(message = "{auth.signup.firstname.required}")
    @Size(min = 1, max = 50, message = "{auth.signup.firstname.size}")
    private String firstName;

    @NotBlank(message = "{auth.signup.lastname.required}")
    @Size(min = 1, max = 50, message = "{auth.signup.lastname.size}")
    private String lastName;

    // Optional fields
    private String avatarUrl;
}