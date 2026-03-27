package com.restaurant.authservice.core.service.auth.dto;

import com.restaurant.commons.constant.ContactType;
import com.restaurant.commons.constant.NotificationPurpose;
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
public class ResendRequest {
    @NotBlank(message = "{auth.resend.identifier.required}")
    private String identifier;   // email or phone number

    @NotNull(message = "{auth.resend.contact_type.required}")
    private ContactType contactType;

    @NotNull(message = "{auth.resend.purpose.required}")
    private NotificationPurpose purpose;  // VERIFY, RESET_PASSWORD, etc.
}
