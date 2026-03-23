package com.restaurant.businessservice.core.rpc;

import com.google.protobuf.ByteString;
import com.restaurant.commons.core.enums.FileCategory;
import com.restaurant.commons.core.rpc.storage.*;
import com.restaurant.commons.core.rpc.user.GetUsersByIdsRequest;
import com.restaurant.commons.core.rpc.user.GetUsersByIdsResponse;
import com.restaurant.commons.core.rpc.user.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BusinessServiceRpcClientImpl implements IBusinessServiceRpcClient {
    private static final Logger _log = LoggerFactory.getLogger(BusinessServiceRpcClientImpl.class);

    @DubboReference
    private StorageService _storageService;

    @DubboReference
    private UserService _userService;

    @Override
    public String createFileOnCloud(MultipartFile file, FileCategory fileCategory, String bucketName, String entityId) {
        if (file == null) return null;
        try{
            _log.info("createFileOnCloud, Prering call gRPC to upload file");
            FileMetadata metadata = FileMetadata.newBuilder()
                    .setBucketName(bucketName)
                    .setEntityId(entityId)
                    .setFileCategory(fileCategory.name())
                    .setContentLength(file.getSize())
                    .setFileName(file.getOriginalFilename())
                    .setFileType(file.getContentType())
                    .build();

            UploadFileRequest request = UploadFileRequest.newBuilder()
                    .setMedatadata(metadata)
                    .setFileData(ByteString.copyFrom(file.getBytes()))
                    .build();

            UploadFileResponse response = _storageService.uploadFile(request);
            _log.info("createFileOnCloud, File created: {}", response.getCdnUrl());
            return response.getCdnUrl();
        }catch (RpcException e){
            _log.error("createFileOnCloud, Cannot create file: {}",e.getMessage());
            return null;
        } catch (IOException e) {
            _log.error("createFileOnCloud, Error during get file bytes: {}",e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> createFilesOnCloud(List<MultipartFile> files) {
        return null;
    }

    @Override
    public GetUsersByIdsResponse getUsersByIds(List<UUID> ids) {
        if(CollectionUtils.isEmpty(ids)) return null;
        try{
            List<String> stringIds = ids.stream().map(UUID::toString).toList();
            GetUsersByIdsRequest request = GetUsersByIdsRequest.newBuilder()
                    .addAllIds(stringIds)
                    .build();

            return _userService.getUsersByIds(request);
        }catch (Exception e){
            _log.error("getUsersByIds, Error during get users ids: {}",e.getMessage());
            return null;
        }
    }
}
