package com.restaurant.businessservice.core.rpc;

import com.google.protobuf.ByteString;
import com.restaurant.commons.core.rpc.storage.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BusinessServiceRpcClientImpl implements IBusinessServiceRpcClient {
    private static final Logger _log = LoggerFactory.getLogger(BusinessServiceRpcClientImpl.class);

    @DubboReference
    private StorageService _storageService;

    @Override
    public String createFileOnCloud(MultipartFile file, String bucketName, String fileCategory) {
        if (file == null) return null;
        try{
            _log.info("createFileOnCloud, Prering call gRPC to upload file");
            FileMetadata metadata = FileMetadata.newBuilder()
                    .setBucketName(bucketName)
                    .setFileName(file.getOriginalFilename())
                    .setFileType(file.getContentType())
                    .setContentLength(file.getSize())
                    .setObjectName(fileCategory + "/" + file.getOriginalFilename())
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
}
