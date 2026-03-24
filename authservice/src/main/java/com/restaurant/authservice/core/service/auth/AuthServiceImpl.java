package com.restaurant.authservice.core.service.auth;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.restaurant.authservice.core.kafka.KafkaProducer;
import com.restaurant.authservice.core.rpc.IUserServiceRpcClient;
import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.authservice.core.service.blacklist.IBackListService;
import com.restaurant.authservice.core.service.jwt.IJwtService;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.constant.ContactType;
import com.restaurant.commons.core.enums.NotiType;
import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import com.restaurant.commons.core.rpc.user.*;
import com.restaurant.commons.exception.AppException;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

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

            if (!createdUser.getSuccess()) {
                _log.error("User creation failed in user-service for email: {}", request.getEmail());
                throw new AppException(
                        "user.created.false",
                        Constant.RES3008,
                        HttpStatus.INTERNAL_SERVER_ERROR.name()
                );
            }
            _log.info("Registration successful for email: {}", request.getEmail());

            //Build verify token
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", request.getEmail());
            claims.put("firstName", request.getFirstName());
            claims.put("lastName", request.getLastName());
            claims.put("type", "VERIFY_EMAIL");

            String verifyToken = _jwtService.generateVerifyAccountToken(
                    createdUser.getId(), claims
            );

            String verifyUrl = UriComponentsBuilder
                    .fromUriString("http://localhost:8081")  // "http://localhost:8081"
                    .path("/api/v1/auth/verify")
                    .queryParam("token", verifyToken)
                    .queryParam("type", ContactType.EMAIL.name())
                    .toUriString();

            // Build metadata map
            Map<String, Value> metadataFields = new HashMap<>();
            metadataFields.put("firstName", Value.newBuilder().setStringValue(request.getFirstName()).build());
            metadataFields.put("lastName",  Value.newBuilder().setStringValue(request.getLastName()).build());
            metadataFields.put("verifyUrl", Value.newBuilder().setStringValue(verifyUrl).build());

            Struct metadata = Struct.newBuilder()
                    .putAllFields(metadataFields)
                    .build();

            SendEmailEvent emailEvent = SendEmailEvent.newBuilder()
                    .setRecipient(request.getEmail())
                    .setUserId(createdUser.getId())
                    .setRecipientId(createdUser.getId())
                    .addTo(request.getEmail())
                    .setSubject("Please verify your account!")
                    .setMetadata(metadata)
                    .setFrom("no-reply@restaurant.com")
                    .setType(NotiType.SYSTEM.name())
                    .build();                                  // userId is optional — set only if available

            _log.info("signUp, about to send Kafka event for email: {}", request.getEmail());
            _authEventProducer.pushUserCreatedEvent(createdUser.getId() , emailEvent, new HashMap<>() );
            _log.info("signUp, Kafka event dispatched for email: {}", request.getEmail());

            return SignupResponse.builder()
                    .id(createdUser.getId())
                    .email(createdUser.getEmail())
                    .firstName(createdUser.getFirstName())
                    .lastName(createdUser.getLastName())
                    .phoneNumber(createdUser.getPhoneNumber())
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
    public VerifyAccountResponse verifyAccountThroughEmail(VerifyAccountRequest request) {
        _log.info("verifyAccountThroughEmail, type={}", request.getType());

        if (!_jwtService.isValidToken(request.getToken())) {
            _log.error("verifyAccountThroughEmail, invalid token {}", request.getToken());
            throw new AppException(
                    "auth.verify.token.invalid",
                    Constant.RES3005,
                    HttpStatus.BAD_REQUEST.name()
            );
        }

        if (this._blackListService.isBlacklisted(request.getToken())) {
            _log.warn("verifyAccountThroughEmail, token is blacklisted");
            throw new AppException(
                    "auth.verify.token_invalid",
                    Constant.RES3005,
                    HttpStatus.BAD_REQUEST.name()
            );
        }

        String userId = _jwtService.extractSubject(request.getToken());

        FoundUserResponse user;

        try {
            user = _userServiceRpcClient.findById(userId);
        } catch (RpcException rpcEx) {
            _log.error("verifyAccountThroughEmail, User-service RPC failed during refreshToken. id={}",
                    userId, rpcEx);
            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        if(!user.getFound()){
            _log.error("verifyAccountThroughEmail, failed. User with id {} is not found", userId);
            throw new AppException(
                    "auth.verify.user_not_found",
                    Constant.RES3002,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        if (!user.getIsActive() || user.getIsDeleted()) {
            _log.error("verifyAccountThroughEmail, failed. Invalid token with userId {}", userId);
            throw new AppException(
                    "auth.verify.token_invalid",
                    Constant.RES3007,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        if (user.getIsVerified()) {
            _log.info("verifyAccountThroughEmail, account already verified for userId={}", userId);
            return VerifyAccountResponse.builder()
                    .type(ContactType.EMAIL)
                    .build();
        }

        _blackListService.blacklistToken(request.getToken());

        VerifyAccountRpcResponse verifiedUser;
        VerifyAccountRpcRequest verifyAccountRpcRequest =
                VerifyAccountRpcRequest.newBuilder().setId(userId).build();
        try {
           verifiedUser = _userServiceRpcClient.verifyAccount(verifyAccountRpcRequest);
        } catch (RpcException rpcEx) {
            _log.error("verifyAccountThroughEmail, RPC failed marking email verified for userId={}", userId, rpcEx);
            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        if(verifiedUser.getUserNotFound()){
            _log.error("verifyAccountThroughEmail, failed. User with id {} is not found", userId);
            throw new AppException(
                    "auth.verify.user_not_found",
                    Constant.RES3002,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        _log.info("verifyAccountThroughEmail, successfully verified email for userId={}", userId);

        return VerifyAccountResponse.builder()
                .type(ContactType.EMAIL)
                .email(verifiedUser.getEmail())
                .firstName(verifiedUser.getFirstName())
                .lastName(verifiedUser.getFirstName())
                .phoneNumber(verifiedUser.getPhoneNumber())
                .build();
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
