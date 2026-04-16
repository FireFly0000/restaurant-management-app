package com.restaurant.userservice.core.rpc;

import com.restaurant.commons.core.rpc.storage.UploadFileRequest;
import com.restaurant.commons.core.rpc.storage.UploadFileResponse;

public interface IUserServiceRpcClient {
    UploadFileResponse uploadFile(UploadFileRequest request);
}
