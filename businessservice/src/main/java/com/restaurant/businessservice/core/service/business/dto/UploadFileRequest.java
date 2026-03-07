package com.restaurant.businessservice.core.service.business.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFileRequest {
    private MultipartFile file;
}
