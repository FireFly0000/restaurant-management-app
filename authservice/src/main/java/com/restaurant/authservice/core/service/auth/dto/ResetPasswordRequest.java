package com.restaurant.authservice.core.service.auth.dto;

import com.restaurant.commons.constant.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    @NotBlank(message = "{auth.reset.password.token.required}")
    private String token;

    @NotBlank(message = "{auth.reset.password.new.password.required}")
    @Size(min = 8, max = 100, message = "{auth.reset.password.new.password.size}")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "{auth.reset.password.pattern}"
    )
    private String newPassword;

    @NotBlank(message = "{auth.reset.password.new.confirmPassword.required}")
    private String confirmNewPassword;

    @NotNull(message = "{auth.reset.password.contact.type.required}")
    private ContactType type;           // EMAIL or PHONE
}
