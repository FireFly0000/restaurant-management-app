package com.restaurant.businessservice.core.service.location.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
public class CreateLocationSettingRequest {
    @NotBlank(message = "{location_setting.setting_name.not-blank}")
    @Length(min = 3, max = 225, message = "{location_setting.setting_name.length}")
    private String settingName;
    @NotBlank(message = "{location_setting.opening_hours.not-blank}")
    private String openingHours;
    @NotNull(message = "{location_setting.has_parking.not-null}")
    private Boolean hasParking;
    @NotNull(message = "{location_setting.seating_capacity.not-null}")
    @Min(value = 1, message = "{location_setting.seating_capacity.min}")
    private Integer seatingCapacity;
    @NotNull(message = "{location_setting.accept_reservation.not-null}")
    private Boolean acceptReservation;
}
