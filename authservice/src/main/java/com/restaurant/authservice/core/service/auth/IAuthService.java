package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.commons.exception.AppException;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    Boolean signUp(RegisterRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    Boolean forgotPassword(ForgotPasswordRequest request);
    Boolean resetPassword(ResetPasswordRequest request);
    Boolean validateToken(ValidateTokenRequest request);
}
