package com.restaurant.userservice.core.rpc;

import com.restaurant.commons.core.rpc.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceRpcClientImpl implements IUserServiceRpcClient {
    private static final Logger _log = LoggerFactory.getLogger(UserServiceRpcClientImpl.class);

    @DubboReference(
            version = "1.0.0",
            group = "user-service",
            protocol = "tri",
            timeout = 3000,
            retries = 2,
            check = false
    )
    private StorageService _storageServiceStub;

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) {
        _log.info("Calling storage-service via Dubbo: uploadFile");
        return _storageServiceStub.uploadFile(request);
    }
}
