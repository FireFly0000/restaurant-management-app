package com.restaurant.authservice.core.rpc;

import com.restaurant.commons.core.rpc.user.CreateUserResponse;
import com.restaurant.commons.core.rpc.user.FindByEmailResponse;

public interface IUserServiceRpcClient {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    CreateUserResponse createUser(
            String email,
            String hashedPassword,
            String phoneNumber,
            String firstName,
            String lastName,
            String avatarUrl
    );
    FindByEmailResponse findByEmail( String email);
}
