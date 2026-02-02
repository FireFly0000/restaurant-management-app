package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.service.auth.dto.*;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {
    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public Boolean register(RegisterRequest request) {
        return null;
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
