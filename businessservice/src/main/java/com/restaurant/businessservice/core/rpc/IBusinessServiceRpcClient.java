package com.restaurant.businessservice.core.rpc;

import com.restaurant.commons.core.enums.FileCategory;
import com.restaurant.commons.core.rpc.user.GetUsersByIdsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IBusinessServiceRpcClient {
    String createFileOnCloud(MultipartFile file, FileCategory fileCategory, String bucketName, String entityId);
    List<String> createFilesOnCloud(List<MultipartFile> files);
    GetUsersByIdsResponse getUsersByIds(List<UUID> ids);
}
