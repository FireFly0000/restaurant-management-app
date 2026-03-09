package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.service.auth.dto.*;

public interface IAuthService {
    AuthResponse signIn(LoginRequest request);
    SignupResponse signUp(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
    Boolean forgotPassword(ForgotPasswordRequest request);
    Boolean resetPassword(ResetPasswordRequest request);
    Boolean validateToken(ValidateTokenRequest request);
}
