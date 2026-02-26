package com.restaurant.authservice.core.rpc;

import com.restaurant.commons.core.rpc.user.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceRpcClient {

    @DubboReference(
            version = "1.0.0",
            group = "user-service",
            protocol = "tri",
            timeout = 3000,
            retries = 2,
            check = false
    )
    private UserService userServiceStub;  //
    private final Logger _log = LoggerFactory.getLogger(UserServiceRpcClient.class);

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        _log.info("Calling user-service via Dubbo: existsByEmail({})", email);
        try {
            ExistsByEmailRequest request = ExistsByEmailRequest.newBuilder()
                    .setEmail(email)
                    .build();

            ExistsResponse response = userServiceStub.existsByEmail(request);
            return response.getExists();
        } catch (RpcException e) {
            _log.error("Failed to check if email exists: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user exists by phone number
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        _log.info("Calling user-service via Dubbo: existsByPhoneNumber({})", phoneNumber);
        try {
            ExistsByPhoneNumberRequest request = ExistsByPhoneNumberRequest.newBuilder()
                    .setPhoneNumber(phoneNumber)
                    .build();

            ExistsResponse response = userServiceStub.existsByPhoneNumber(request);
            return response.getExists();
        } catch (Exception e) {
            _log.error("Failed to check if phone number exists: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean createUser(
            String email,
            String hashedPassword,
            String phoneNumber,
            String firstName,
            String lastName,
            String avatarUrl
    ){
        _log.info("Calling user-service via Dubbo: createUser({})", email);
        try {
            CreateUserRequest request = CreateUserRequest.newBuilder()
                    .setEmail(email)
                    .setPassword(hashedPassword)
                    .setPhoneNumber(phoneNumber != null ? phoneNumber : "")
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setAvatarUrl(avatarUrl != null ? avatarUrl : "")
                    .build();

            CreateUserResponse response = userServiceStub.createUser(request);
            return response.getSuccess();
        } catch (RpcException e) {
            _log.error("Failed to create user: {}", e.getMessage(), e);
            return false;
        }
    }
}