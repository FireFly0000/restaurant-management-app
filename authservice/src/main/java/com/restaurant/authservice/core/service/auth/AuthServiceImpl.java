package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.rpc.UserServiceRpcClient;
import com.restaurant.authservice.core.service.auth.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserServiceRpcClient userServiceRpcClient;
    private final Logger _log = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(
            UserServiceRpcClient userServiceRpcClient
    ){
        this.userServiceRpcClient = userServiceRpcClient;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public Boolean register(RegisterRequest request) {
        _log.info("Starting registration process for email: {}", request.getEmail());

        try {
            // Step 1: Check if email already exists via RPC call to user-service
            _log.info("Checking if email exists: {}", request.getEmail());
            boolean emailExists = userServiceRpcClient.existsByEmail(request.getEmail());

            if (emailExists) {
                _log.warn("Registration failed: Email already exists - {}", request.getEmail());
                // TODO: You might want to throw a custom exception here instead
                return false;
            }

            _log.info("Email is available: {}", request.getEmail());

            // TODO: Continue with registration logic (validate, hash password, create user, etc.)
            // For now, just return true to indicate email check passed

            return true;

        } catch (Exception e) {
            _log.error("Error during registration for email: {}", request.getEmail(), e);
            // TODO: Handle RPC exceptions appropriately
            throw new RuntimeException("Registration failed due to service error", e);
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }

    @Override
    public Boolean forgotPassword(ForgotPasswordRequest request) {
        return null;
    }

    @Override
    public Boolean resetPassword(ResetPasswordRequest request) {
        return null;
    }

    @Override
    public Boolean validateToken(ValidateTokenRequest request) {
        return null;
    }
}
