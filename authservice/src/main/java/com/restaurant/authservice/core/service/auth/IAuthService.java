package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.service.auth.dto.*;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    Boolean signUp(RegisterRequest request);
    VerifyAccountResponse verifyAccountThroughEmail(VerifyAccountRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    Boolean forgotPassword(ForgotPasswordRequest request);
    Boolean resetPassword(ResetPasswordRequest request);
    Boolean validateToken(ValidateTokenRequest request);
}
