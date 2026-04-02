package com.restaurant.authservice.core.rpc;

import com.restaurant.commons.core.rpc.user.*;

public interface IUserServiceRpcClient {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    CreateUserResponse createUser(
        CreateUserRequest request
    );
    FoundUserResponse findByEmail(String email);
    FoundUserResponse findById(String id);
    VerifyAccountRpcResponse verifyAccount(VerifyAccountRpcRequest request);
}
