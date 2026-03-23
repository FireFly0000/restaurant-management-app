package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.core.kafka.KafkaProducer;
import com.restaurant.authservice.core.rpc.IUserServiceRpcClient;
import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.authservice.core.service.blacklist.IBackListService;
import com.restaurant.authservice.core.service.jwt.IJwtService;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.rpc.user.CreateUserRequest;
import com.restaurant.commons.core.rpc.user.CreateUserResponse;
import com.restaurant.commons.core.rpc.user.FoundUserResponse;
import com.restaurant.commons.exception.AppException;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUserServiceRpcClient _userServiceRpcClient;
    private final Logger _log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder _passwordEncoder;
    private final IJwtService _jwtService;
    private final KafkaProducer _authEventProducer;
    private final IBackListService _blackListService;

    public AuthServiceImpl(
            IUserServiceRpcClient userServiceRpcClient,
            PasswordEncoder passwordEncoder,
            KafkaProducer authEventProducer,
            IJwtService jwtService,
            IBackListService backListService
    ){
        this._userServiceRpcClient = userServiceRpcClient;
        this._passwordEncoder = passwordEncoder;
        this._jwtService = jwtService;
        this._authEventProducer = authEventProducer;
        this._blackListService = backListService;
    }

    @Override
    public AuthResponse signIn(LoginRequest request) {
        _log.info("signIn, Starting signing in process for email: {}", request.getEmail());

        FoundUserResponse response;

        try{
            response = _userServiceRpcClient.findByEmail(request.getEmail());
        }catch (RpcException rpcEx){
            _log.error("signIn, User-service RPC failed during sign in. Email={}",
                    request.getEmail(), rpcEx);

            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        // check found flag first
        if (!response.getFound()) {
            _log.error("SignIn, failed. User with email {} is not found", request.getEmail());
            throw new AppException(
                    "auth.signin.user_not_found",
                    Constant.RES3002,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        if(!response.getIsActive()){
          _log.error("SignIn, failed. User with email {} is not active", request.getEmail());
          throw new AppException(
                  "auth.signin.account_inactive",
                  Constant.RES3007,
                  HttpStatus.FORBIDDEN.name()
          );
        }

        if(!response.getIsVerified()){
            _log.error("SignIn, failed. User with email {} is not verified", request.getEmail());
            throw new AppException(
                    "auth.signin.unverified_account",
                    Constant.RES3007,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        if(response.getIsDeleted()){
            _log.error("SignIn, failed. User with email {} is deleted", request.getEmail());
            throw new AppException(
                    "auth.signin.deleted_account",
                    Constant.RES3007,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        if(!_passwordEncoder.matches(request.getPassword(), response.getPassword())){
            _log.error("SignIn, failed. Wrong password");
            throw new AppException(
                    "auth.signin.invalid_password",
                    Constant.RES3008,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", response.getEmail());
        claims.put("userType", response.getUserType());

        String accessToken = _jwtService.generateAccessToken(
                response.getId(), claims
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
    public SignupResponse signUp(SignupRequest request) {
        _log.info("signUp, Starting registration process for email: {}", request.getEmail());

        try{
            _log.info("signUp, Checking if email exists: {}", request.getEmail());
            boolean emailExists = _userServiceRpcClient.existsByEmail(request.getEmail());
            if (emailExists) {
                _log.warn("signUp, failed: Email already exists - {}", request.getEmail());
                throw new AppException(
                        "auth.signup.email_exists",
                        Constant.RES3005,
                        HttpStatus.BAD_REQUEST.name()
                );
            }

            _log.info("signUp, checking if phone number exists: {}", request.getPhoneNumber());
            boolean phoneNumberExists = _userServiceRpcClient.existsByPhoneNumber(request.getPhoneNumber());
            if (phoneNumberExists) {
                _log.warn("signUp, failed: Phone number already exists - {}", request.getPhoneNumber());
                throw new AppException(
                        "auth.signup.phone_exists",
                        Constant.RES3005,
                        HttpStatus.BAD_REQUEST.name()
                );
            }

            String hashedPassword = _passwordEncoder.encode(request.getPassword());

            CreateUserRequest rpcRequest = CreateUserRequest.newBuilder()
                    .setEmail(request.getEmail())
                    .setPassword(hashedPassword)
                    .setPhoneNumber(
                            request.getPhoneNumber() != null ?
                                    request.getPhoneNumber() :
                                    ""
                    )
                    .setFirstName(request.getFirstName())
                    .setLastName(request.getLastName())
                    .setAvatarUrl(
                            request.getAvatarUrl() != null ?
                                    request.getAvatarUrl() :
                                    ""
                    )
                    .build();

            CreateUserResponse createdUser = _userServiceRpcClient.createUser(rpcRequest);

            _log.info("signUp, successful for email: {}", request.getEmail());

            return SignupResponse.builder()
                    .id(createdUser.getId())
                    .email(createdUser.getEmail())
                    .firstName(createdUser.getFirstName())
                    .lastName(createdUser.getLastName())
                    .phoneNumber(createdUser.getPhoneNumber())
                    .build();

        }catch (RpcException rpcEx){
            _log.error("signUp, User-service RPC failed during registration. Email={}",
                    request.getEmail(), rpcEx);

            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }
    }

    @Override
    public VerifyAccountResponse verifyAccountThroughEmail(VerifyAccountRequest request) {
        _log.info("verifyContact, type={}", request.getType());

        //check if token is valid
        if (!_jwtService.isValidToken(request.getToken())) {
            _log.error("verifyAccountThroughEmail, invalid token {}", request.getToken());
            throw new AppException(
                    "auth.verify.token.invalid",
                    Constant.RES3005,
                    HttpStatus.BAD_REQUEST.name()
            );
        }

        // Check if token is blacklisted
        if (this._blackListService.isBlacklisted(request.getToken())) {
            _log.warn("verifyAccountThroughEmail, token is blacklisted");
            throw new AppException(
                    "auth.verify.token.invalid",
                    Constant.RES3005,
                    HttpStatus.BAD_REQUEST.name()
            );
        }

        String userId = _jwtService.extractSubject(request.getToken());

        FoundUserResponse user;

        try {
            user = _userServiceRpcClient.findById(userId);
        } catch (RpcException rpcEx) {
            _log.error("verifyAccountThroughEmail, User-service RPC failed");
        }
        return null;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        _log.info("refreshToken, processing refresh token request");

        if (!_jwtService.isValidToken(refreshToken)) {
            _log.error("refreshToken, invalid refreshToken {}", refreshToken);
            throw new AppException(
                    "auth.refresh.token_invalid",
                    Constant.RES3008,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        String userId = _jwtService.extractSubject(refreshToken);

        FoundUserResponse user;

        try {
            user = _userServiceRpcClient.findById(userId);
        } catch (RpcException rpcEx) {
            _log.error("refreshToken, User-service RPC failed during refreshToken. id={}",
                    userId, rpcEx);
            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        if(!user.getFound()){
            _log.error("refreshToken, failed. User with id {} is not found", userId);
            throw new AppException(
                    "auth.signin.user_not_found",
                    Constant.RES3002,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        if (!user.getIsActive() || !user.getIsVerified() || user.getIsDeleted()) {
            _log.error("refreshToken, failed. Invalid token with userId {}", userId);
            throw new AppException(
                    "auth.refresh.token_invalid",
                    Constant.RES3007,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userType", user.getUserType());

        String newAccessToken = _jwtService.generateAccessToken(
                user.getId(), claims
        );

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
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
