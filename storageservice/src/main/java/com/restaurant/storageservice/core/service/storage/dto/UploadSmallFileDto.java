package com.restaurant.storageservice.core.service.storage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class UploadSmallFileDto extends UploadFileDto {
    private byte[] file;
}
