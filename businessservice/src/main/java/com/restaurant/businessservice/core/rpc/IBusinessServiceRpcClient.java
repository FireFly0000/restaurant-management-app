package com.restaurant.businessservice.core.rpc;

import com.restaurant.commons.core.enums.FileCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBusinessServiceRpcClient {
    String createFileOnCloud(MultipartFile file, FileCategory fileCategory, String bucketName, String entityId);
    List<String> createFilesOnCloud(List<MultipartFile> files);
}
