package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.rpc.IUserServiceRpcClient;
import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.authservice.core.service.jwt.IJwtService;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.rpc.user.CreateUserResponse;
import com.restaurant.commons.core.rpc.user.FoundUserResponse;
import com.restaurant.commons.exception.AppException;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUserServiceRpcClient _userServiceRpcClient;
    private final Logger _log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder _passwordEncoder;
    private final IJwtService _jwtService;

    public AuthServiceImpl(
            IUserServiceRpcClient userServiceRpcClient,
            PasswordEncoder passwordEncoder,
            IJwtService jwtService
    ){
        this._userServiceRpcClient = userServiceRpcClient;
        this._passwordEncoder = passwordEncoder;
        this._jwtService = jwtService;
    }

    @Override
    public AuthResponse signIn(LoginRequest request) {
        _log.info("Starting signing in process for email: {}", request.getEmail());

        FoundUserResponse response;

        try{
            response = _userServiceRpcClient.findByEmail(request.getEmail());
        }catch (RpcException rpcEx){
            _log.error("User-service RPC failed during sign in. Email={}",
                    request.getEmail(), rpcEx);

            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        if(!response.getIsActive()){
          _log.error("Sign in failed. User with email {} is not active", request.getEmail());
          throw new AppException(
                  "auth.signin.account_inactive",
                  Constant.RES3010,
                  HttpStatus.FORBIDDEN.name()
          );
        }

        if(response.getIsVerified()){
            _log.error("Sign in failed. User with email {} is not verified", request.getEmail());
            throw new AppException(
                    "auth.signin.unverified_account",
                    Constant.RES3011,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        if(!_passwordEncoder.matches(request.getPassword(), response.getPassword())){
            _log.error("Sign in failed. Wrong password");
            throw new AppException(
                    "auth.signin.invalid_password",
                    Constant.RES3012,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        String accessToken = _jwtService.generateAccessToken(
                response.getId(),
                response.getEmail(),
                response.getUserType()
        );

        String refreshToken = _jwtService.generateRefreshToken(
                response.getId()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public SignupResponse signUp(RegisterRequest request) {
        _log.info("Starting registration process for email: {}", request.getEmail());

        try{
            _log.info("Checking if email exists: {}", request.getEmail());
            boolean emailExists = _userServiceRpcClient.existsByEmail(request.getEmail());
            if (emailExists) {
                _log.warn("Registration failed: Email already exists - {}", request.getEmail());
                throw new AppException("auth.signup.email_exists", Constant.RES3006, HttpStatus.BAD_REQUEST.name());
            }

            _log.info("Checking if phone number exists: {}", request.getPhoneNumber());
            boolean phoneNumberExists = _userServiceRpcClient.existsByPhoneNumber(request.getPhoneNumber());
            if (phoneNumberExists) {
                _log.warn("Registration failed: Phone number already exists - {}", request.getPhoneNumber());
                throw new AppException("auth.signup.phone_exists", Constant.RES3007, HttpStatus.BAD_REQUEST.name());
            }

            String hashedPassword = _passwordEncoder.encode(request.getPassword());

            CreateUserResponse createdUser = _userServiceRpcClient.createUser(
                    request.getEmail(),
                    hashedPassword,
                    request.getPhoneNumber(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getAvatarUrl()
            );

            _log.info("Registration successful for email: {}", request.getEmail());

            return SignupResponse.builder()
                    .id(createdUser.getId())
                    .email(createdUser.getEmail())
                    .firstName(createdUser.getFirstName())
                    .lastName(createdUser.getLastName())
                    .build();

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
    public AuthResponse refreshToken(String refreshToken) {
        _log.info("Processing refresh token request");

        if (!_jwtService.isValidToken(refreshToken)) {
            throw new AppException(
                    "auth.refresh.token_invalid",
                    Constant.RES3013,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        String userId = _jwtService.extractSubject(refreshToken);

        FoundUserResponse user;

        try {
            user = _userServiceRpcClient.findById(userId);
        } catch (RpcException rpcEx) {
            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        if (!user.getIsActive() || !user.getIsVerified()) {
            throw new AppException(
                    "auth.refresh.token_invalid",
                    Constant.RES3013,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        String newAccessToken = _jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getUserType()
        );
        String newRefreshToken = _jwtService.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
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
