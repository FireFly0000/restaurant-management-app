package com.restaurant.businessservice.core.service.location.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UpdateLocationRequest {
    @NotBlank(message = "{location.branch_name.not-blank}")
    @Length(min = 3, max = 225, message = "{location.branch_name.length}")
    private String branchName;
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "{location.phone.invalid}")
    @NotBlank(message = "{location.phone_number.not-blank}")
    private String phoneNumber;
    @NotBlank(message = "{location.email.not-blank}")
    @Email(message = "{location.email.invalid}")
    private String email;
    @NotNull(message = "{location.start_date.not-null}")
    @FutureOrPresent(message = "{location.start_date.future}")
    private LocalDateTime startDate;
    @NotBlank(message = "{location.address.not-blank}")
    @Length(min = 3, max = 225, message = "{location.address.length}")
    private String address;
}
