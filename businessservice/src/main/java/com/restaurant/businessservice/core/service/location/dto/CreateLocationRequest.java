package com.restaurant.businessservice.core.service.location.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class CreateLocationRequest {
    @NotBlank(message = "{location.business_id.not-blank}")
    private String businessId;
    @NotBlank(message = "{location.branch_name.not-blank}")
    @Length(min = 3, max = 225, message = "{location.branch_name.length}")
    private String branchName;
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "{location.phone.invalid}")
    @NotBlank(message = "{location.phone_number.not-blank}")
    private String phoneNumber;
    @NotBlank(message = "{location.email.not-blank}")
    @Email(message = "{location.email.invalid}")
    private String email;
    private String managerId;

    private LocalDateTime startDate;
    @NotBlank(message = "{location.address.not-blank}")
    @Length(min = 3, max = 225, message = "{location.address.length}")
    private String address;
    @Valid
    @NotNull(message = "{location.location_setting.not-null}")
    private CreateLocationSettingRequest locationSetting;
}
