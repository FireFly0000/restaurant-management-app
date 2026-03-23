package com.restaurant.authservice.core.service.auth.dto;

import com.restaurant.commons.constant.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyAccountRequest {
    @NotNull(message = "{auth.verify.type.required}")
    private ContactType type;           // EMAIL or PHONE

    @NotBlank(message = "{auth.verify.token.required}")
    private String token;               // verification token sent to user
}
