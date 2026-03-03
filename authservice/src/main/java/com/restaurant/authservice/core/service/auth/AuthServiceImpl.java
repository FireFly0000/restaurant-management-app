package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.rpc.UserServiceRpcClient;
import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.exception.AppException;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    public Boolean signUp(RegisterRequest request) {
        _log.info("Starting registration process for email: {}", request.getEmail());

        // Step 1: Validate passwords match
        if (!request.passwordsMatch()) {
            _log.warn("Registration failed: Passwords do not match for email: {}", request.getEmail());
            throw new AppException("auth.signup.password_not_match", Constant.RES3005, HttpStatus.BAD_REQUEST.name());
        }

        try{
            // Step 2: Check if email already exists via RPC
            _log.info("Checking if email exists: {}", request.getEmail());
            boolean emailExists = userServiceRpcClient.existsByEmail(request.getEmail());
            if (emailExists) {
                _log.warn("Registration failed: Email already exists - {}", request.getEmail());
                throw new AppException("auth.signup.email_exists", Constant.RES3006, HttpStatus.BAD_REQUEST.name());
            }

            // Step 3: Check if phone number already exists via RPC
            _log.info("Checking if phone number exists: {}", request.getPhoneNumber());
            boolean phoneNumberExists = userServiceRpcClient.existsByPhoneNumber(request.getPhoneNumber());
            if (phoneNumberExists) {
                _log.warn("Registration failed: Phone number already exists - {}", request.getPhoneNumber());
                throw new AppException("auth.signup.phone_exists", Constant.RES3007, HttpStatus.BAD_REQUEST.name());
            }

            String hashedPassword = _passwordEncoder.encode(request.getPassword());

            // Step 4: Create user via RPC call to user-service
            boolean created = userServiceRpcClient.createUser(
                    request.getEmail(),
                    hashedPassword,
                    request.getPhoneNumber(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getAvatarUrl()
            );

            if (!created) {
                _log.error("User creation failed in user-service for email: {}", request.getEmail());
                throw new AppException(
                        "user.created.false",
                        Constant.RES3008,
                        HttpStatus.INTERNAL_SERVER_ERROR.name()
                );
            }

            _log.info("Registration successful for email: {}", request.getEmail());
            return true;
        }catch (RpcException rpcEx){
            _log.error("User-service RPC failed during registration. Email={}",
                    request.getEmail(), rpcEx);

            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
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
