package com.restaurant.storageservice.core.service.storage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class UploadFileDto {
    private String bucketName;
    private String objectKey;
    private String contentType;
    private long contentLength;
}
