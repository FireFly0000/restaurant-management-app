package com.restaurant.storageservice.core.service.storage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.InputStream;

@SuperBuilder
@Getter
@Setter
public class UploadLargeFileDto extends UploadFileDto {
    private InputStream stream;
}
