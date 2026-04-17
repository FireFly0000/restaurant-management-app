package com.restaurant.userservice.core.service.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePhoneNumberRequest {
    @NotBlank(message = "{user.update.phone.required}")
    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "{user.update.phone.invalid}"
    )
    private String phoneNumber;
}
