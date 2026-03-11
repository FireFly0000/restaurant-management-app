package com.restaurant.storageservice.core.service.storage;

import com.restaurant.storageservice.core.service.storage.dto.UploadLargeFileDto;
import com.restaurant.storageservice.core.service.storage.dto.UploadSmallFileDto;

import java.util.List;

public interface IStorageService {
    boolean createBucket(String argBucketName);
    boolean deleteFile(String argBucketName, String argObjectKey);
    boolean deleteFiles(String argBucketName, List<String> argObjectKeys);
    byte[] downloadFile(String argBucketName, String argObjectKey);
    String uploadFile(UploadSmallFileDto argFile);
    String getPublicCdnUrl(String argObjectKey);
    String uploadStream(UploadLargeFileDto argFile);
}
