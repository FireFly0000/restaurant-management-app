package com.restaurant.userservice.core.rpc;

public interface IUserRpcService {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}

