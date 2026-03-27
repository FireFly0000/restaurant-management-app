package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.service.auth.dto.*;

public interface IAuthService {
    VerifyAccountResponse verifyAccount(VerifyAccountRequest request);
    ResendResponse resend(ResendRequest request);
    AuthResponse signIn(LoginRequest request);
    SignupResponse signUp(SignupRequest request);
    AuthResponse refreshToken(String refreshToken);
    Boolean forgotPassword(ForgotPasswordRequest request);
    Boolean resetPassword(ResetPasswordRequest request);
    Boolean validateToken(ValidateTokenRequest request);
}
