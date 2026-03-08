package com.restaurant.authservice.core.rpc;

public interface IUserServiceRpcClient {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean createUser(
            String email,
            String hashedPassword,
            String phoneNumber,
            String firstName,
            String lastName,
            String avatarUrl
    );
}
