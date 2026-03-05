package com.restaurant.businessservice.core.rpc;

import com.restaurant.commons.core.rpc.storage.CreateFileOnCloudRequest;
import com.restaurant.commons.core.rpc.storage.CreateFileOnCloudResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBusinessServiceRpcClient {
    String createFileOnCloud(MultipartFile file);
    List<String> createFilesOnCloud(List<MultipartFile> files);
}
