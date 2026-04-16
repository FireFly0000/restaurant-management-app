package com.restaurant.userservice.core.service.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoRequest {
    @NotBlank(message = "{user.update.firstname.required}")
    @Size(min = 1, max = 50, message = "{user.update.firstname.size}")
    private String firstName;

    @NotBlank(message = "{user.update.lastname.required}")
    @Size(min = 1, max = 50, message = "{user.update.lastname.size}")
    private String lastName;

    @Past(message = "{user.update.birthdate.past}")
    private LocalDate birthDate;
}
