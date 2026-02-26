package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.rpc.UserServiceRpcClient;
import com.restaurant.authservice.core.service.auth.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserServiceRpcClient userServiceRpcClient;
    private final Logger _log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder _passwordEncoder;

    public AuthServiceImpl(
            UserServiceRpcClient userServiceRpcClient,
            PasswordEncoder passwordEncoder
    ){
        this.userServiceRpcClient = userServiceRpcClient;
        this._passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public Boolean register(RegisterRequest request) {
        _log.info("Starting registration process for email: {}", request.getEmail());

        try {
            // Step 1: Validate passwords match
            if (!request.passwordsMatch()) {
                _log.warn("Registration failed: Passwords do not match for email: {}", request.getEmail());
                throw new IllegalArgumentException("Passwords do not match");
            }

            // Step 2: Check if email already exists via RPC
            _log.info("Checking if email exists: {}", request.getEmail());
            boolean emailExists = userServiceRpcClient.existsByEmail(request.getEmail());
            if (emailExists) {
                _log.warn("Registration failed: Email already exists - {}", request.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }

            // Step 3: Check if phone number already exists via RPC
            _log.info("Checking if phone number exists: {}", request.getPhoneNumber());
            boolean phoneNumberExists = userServiceRpcClient.existsByPhoneNumber(request.getPhoneNumber());
            if (phoneNumberExists) {
                _log.warn("Registration failed: Phone number already exists - {}", request.getPhoneNumber());
                throw new IllegalArgumentException("Phone number already exists");
            }

            // Step 4: Hash the password
            String hashedPassword = _passwordEncoder.encode(request.getPassword());

            // Step 5: Create user via RPC call to user-service
            boolean created = userServiceRpcClient.createUser(
                    request.getEmail(),
                    hashedPassword,
                    request.getPhoneNumber(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getAvatarUrl()
            );

            if (!created) {
                _log.error("Registration failed: Could not create user for email: {}", request.getEmail());
                throw new RuntimeException("Failed to create user");
            }

            _log.info("Registration successful for email: {}", request.getEmail());
            return true;

            // TODO: Continue with registration logic (validate, hash password, create user, etc.)
            // For now, just return true to indicate email check passed

        } catch (IllegalArgumentException e) {
            // Rethrow business logic errors directly without wrapping
            throw e;
        } catch (Exception e) {
            _log.error("Error during registration for email: {}", request.getEmail(), e);
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
