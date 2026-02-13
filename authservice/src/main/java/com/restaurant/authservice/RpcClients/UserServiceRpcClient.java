package com.restaurant.authservice.RpcClients;

import com.restaurant.commons.proto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserServiceRpcClient {

    @DubboReference(
            version = "1.0.0",
            group = "user-service",
            protocol = "tri",           // Triple protocol (gRPC-compatible)
            timeout = 3000,             // 3 seconds timeout
            retries = 2,                // Retry 2 times on failure
            check = false,              // Don't fail on startup if service unavailable
            loadbalance = "roundrobin"  // Load balancing strategy
    )
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;
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
        } catch (Exception e) {
            _log.error("Failed to check if email exists: {}", e.getMessage(), e);
            throw e;
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
}