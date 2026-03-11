package com.restaurant.storageservice.core.rpc;

import com.restaurant.commons.core.rpc.storage.*;
import com.restaurant.storageservice.core.service.storage.IStorageService;
import com.restaurant.storageservice.core.service.storage.dto.UploadLargeFileDto;
import com.restaurant.storageservice.core.service.storage.dto.UploadSmallFileDto;
import io.grpc.Status;
import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@DubboService
public class StorageServiceRpcServerImpl extends DubboStorageServiceTriple.StorageServiceImplBase {

    private static final Logger _log = LoggerFactory.getLogger(StorageServiceRpcServerImpl.class);
    private final IStorageService _storageService;

    public StorageServiceRpcServerImpl(
            IStorageService storageService
    ) {
        this._storageService = storageService;
    }

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request){
        FileMetadata fileMetadata = request.getMedatadata();
        byte[] file = request.getFileData().toByteArray();
        UploadSmallFileDto smallFile = UploadSmallFileDto.builder()
                .bucketName(fileMetadata.getBucketName())
                .objectKey(fileMetadata.getObjectName())
                .contentLength(fileMetadata.getContentLength())
                .contentType(fileMetadata.getMimeType())
                .file(file)
                .build();

        String fileUrl = _storageService.uploadFile(smallFile);
        if(fileUrl == null){
            throw Status.UNKNOWN
                    .withDescription("uploadFile, Upload file failed")
                    .asRuntimeException();
        }

        return UploadFileResponse.newBuilder()
                .setCdnUrl(fileUrl)
                .build();
    }

    @Override
    public StreamObserver<UploadLargeFileRequest> uploadStream(StreamObserver<UploadFileResponse> responseObserver){
        return new StreamObserver<>() {
            private FileMetadata fileMetadata;
            private Path tempFilePath;
            private OutputStream tempFileOutputStream;

            @Override
            public void onNext(UploadLargeFileRequest request) {
                try{
                    if(request.hasMedatadata()){
                        this.fileMetadata = request.getMedatadata();
                        this.tempFilePath = Files.createTempFile("dubbo_upload_",".tmp");
                        this.tempFileOutputStream = Files.newOutputStream(tempFilePath, StandardOpenOption.WRITE);
                    }else if(request.hasChunkData()){
                        if(tempFileOutputStream == null){
                            responseObserver.onError(Status.UNKNOWN
                                    .withDescription("Missing metadata in stream")
                                    .asRuntimeException());
                            fileCleanUp();
                            return;
                        }
                        request.getChunkData().writeTo(tempFileOutputStream);
                    }
                }catch (Exception ex){
                    _log.error("uploadStream, I/O error : {}", ex.getMessage());
                    responseObserver.onError(Status.UNKNOWN
                            .withDescription("I/O error")
                            .asRuntimeException());
                    fileCleanUp();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                _log.error("Connection is break or else: {}", throwable.getMessage());
                fileCleanUp();
            }

            @Override
            public void onCompleted() {
                try{
                    if(tempFileOutputStream != null) tempFileOutputStream.close();
                    try(InputStream inputStream = Files.newInputStream(tempFilePath, StandardOpenOption.READ)){
                        UploadLargeFileDto largeFile = UploadLargeFileDto.builder()
                                .stream(inputStream)
                                .bucketName(fileMetadata.getBucketName())
                                .objectKey(fileMetadata.getObjectName())
                                .contentLength(fileMetadata.getContentLength())
                                .contentType(fileMetadata.getMimeType())
                                .build();
                        String cdnUrl = _storageService.uploadStream(largeFile);
                        if(cdnUrl == null){
                            responseObserver.onError(
                                    Status.UNKNOWN.withDescription("uploadStream, Upload file failed").asRuntimeException()
                            );
                            return;
                        }
                        UploadFileResponse response = UploadFileResponse.newBuilder()
                                .setCdnUrl(cdnUrl)
                                .build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                    }
                }catch (Exception ex){
                    _log.error("uploadStream, Upload file failed : {}", ex.getMessage());
                    responseObserver.onError(
                            Status.UNKNOWN.withDescription(ex.getMessage()).asRuntimeException()
                    );
                } finally {
                    fileCleanUp();
                }
            }

            private void fileCleanUp(){
                try{
                    if(tempFileOutputStream != null) tempFileOutputStream.close();
                    if(tempFilePath != null) Files.deleteIfExists(tempFilePath);
                    _log.info("fileCleanUp, Deleted temp file {}", tempFilePath);
                }catch (Exception ex){
                    _log.error("fileCleanUp, Cannot delete temp file : {}", tempFilePath);
                }
            }
        };
    }
}
