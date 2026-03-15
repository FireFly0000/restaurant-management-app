package com.restaurant.businessservice.core.service.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBusinessRequest {
    @NotBlank(message = "{business.name.not-blank}")
    @Length(min = 3, max = 225, message = "{business.name.length}")
    private String name;
    @NotBlank(message = "{business.business_type.not-blank}")
    private String businessType;
    @Length(max = 1000, message = "{business.description.length}")
    private String description;
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "{business.phone.invalid}")
    @NotBlank(message = "{business.phone_number.not-blank}")
    private String phoneNumber;
    @NotBlank(message = "{business.email.not-blank}")
    @Email(message = "{business.email.invalid}")
    private String email;
    private String websiteUrl;
    private MultipartFile avatarUrl;
    private MultipartFile coverImg;
}
