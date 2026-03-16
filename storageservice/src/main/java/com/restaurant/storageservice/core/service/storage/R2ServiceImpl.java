package com.restaurant.storageservice.core.service.storage;

import com.restaurant.storageservice.config.StorageProperties;
import com.restaurant.storageservice.core.service.storage.dto.UploadLargeFileDto;
import com.restaurant.storageservice.core.service.storage.dto.UploadSmallFileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.List;

@Service
public class R2ServiceImpl implements IStorageService {

    private static final Logger _log = LoggerFactory.getLogger(R2ServiceImpl.class);
    private final S3Client _r2;
    private final StorageProperties _properties;

    public R2ServiceImpl(
            @Qualifier("r2") S3Client _r2,
            StorageProperties _properties
    ) {
        this._r2 = _r2;
        this._properties = _properties;
    }

    @Override
    public boolean createBucket(String argBucketName) {
        return false;
    }

    @Override
    public boolean deleteFile(String argBucketName, String argObjectKey) {
        _log.info("deleteFile, Deleting file {} from bucket {} ", argObjectKey, argBucketName);
        try{
            DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                    .bucket(argBucketName)
                    .key(argObjectKey)
                    .build();
            _r2.deleteObject(deleteReq);
            _log.info("deleteFile, Deleted file {} from bucket {} ", argObjectKey, argBucketName);
            return true;
        }catch(Exception e){
            _log.error("deleteFile, Delete file failed: {}",e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteFiles(String argBucketName, List<String> argObjectKeys) {
        if(argObjectKeys == null || argObjectKeys.isEmpty()) return true;
        _log.info("deleteFiles, Deleting {} files from bucket {} ", argObjectKeys.size() ,argBucketName);

        try{
            List<ObjectIdentifier> identifiers = argObjectKeys.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .toList();

            Delete delete = Delete.builder()
                    .objects(identifiers)
                    .build();

            DeleteObjectsRequest deleteReq = DeleteObjectsRequest.builder()
                    .delete(delete)
                    .bucket(argBucketName)
                    .build();
            DeleteObjectsResponse response = _r2.deleteObjects(deleteReq);
            if(response.hasErrors()){
                for(S3Error error : response.errors()){
                    _log.error("deleteFiles, Cannot delete file [{}] because: {}", error.key(), error.message());
                }
                return false;
            }
            _log.info("deleteFiles, Deleted {} files from bucket {} ", argObjectKeys.size(), argBucketName);
            return true;
        }catch (Exception e){
            _log.error("deleteFiles, Delete files failed: {}",e.getMessage());
            return false;
        }
    }

    @Override
    public byte[] downloadFile(String argBucketName, String argObjectKey) {
        _log.info("downloadFile, Downloading file {} from bucket {} ", argObjectKey, argBucketName);
        try{
            GetObjectRequest getReq = GetObjectRequest.builder()
                    .bucket(argBucketName)
                    .key(argObjectKey)
                    .build();

            ResponseBytes<GetObjectResponse> response = _r2.getObjectAsBytes(getReq);
            _log.info("downloadFile, Downloaded file {} from bucket {} ", argObjectKey, argBucketName);
            return response.asByteArray();
        }catch (Exception e){
            _log.error("downloadFile, Download failed: {}",e.getMessage());
            return null;
        }
    }

    @Override
    public String uploadFile(UploadSmallFileDto argFile) {
        _log.info("uploadFile, Uploading file {} to bucket {} ", argFile.getObjectKey(), argFile.getBucketName());
        try{
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .contentType(argFile.getContentType())
                    .bucket(argFile.getBucketName())
                    .key(argFile.getObjectKey())
                    .build();

            _r2.putObject(putReq, RequestBody.fromBytes(argFile.getFile()));
            _log.info("uploadFile, Uploaded file {} to bucket {} ", argFile.getObjectKey(), argFile.getBucketName());
            return this.getPublicCdnUrl(argFile.getObjectKey());
        }catch (Exception e){
            _log.error("uploadFile, Upload failed: {}",e.getMessage());
            return null;
        }
    }

    @Override
    public String uploadStream(UploadLargeFileDto argFile) {
        _log.info("uploadStream, Uploading file {} to bucket {} ", argFile.getObjectKey(), argFile.getBucketName());
        try(InputStream is = argFile.getStream()){
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(argFile.getBucketName())
                    .key(argFile.getObjectKey())
                    .contentType(argFile.getContentType())
                    .contentLength(argFile.getContentLength())
                    .build();

            _r2.putObject(putReq, RequestBody.fromInputStream(is,  argFile.getContentLength()));
            _log.info("uploadStream, Uploaded file {} to bucket {} ", argFile.getObjectKey(), argFile.getBucketName());
            return this.getPublicCdnUrl(argFile.getObjectKey());
        }catch (Exception e){
            _log.error("uploadStream, Upload failed: {}",e.getMessage());
            return null;
        }
    }

    @Override
    public String getPublicCdnUrl(String argObjectKey) {
        String url = this._properties.getCloudflare().getR2().getObjectPublicUrl();
        String cdn = url.endsWith("/") ? url : url + "/";
        String key = argObjectKey.startsWith("/") ? argObjectKey.substring(1) : argObjectKey;
        return cdn + key;
    }
}
