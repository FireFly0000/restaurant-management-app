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
import com.restaurant.commons.constant.EmailTemplate;
import com.restaurant.commons.constant.NotificationPurpose;
import com.restaurant.commons.core.enums.NotiType;
import com.restaurant.commons.core.rpc.notification.ResendExternalNotificationEvent;
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
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUserServiceRpcClient _userServiceRpcClient;
    private final Logger _log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder _passwordEncoder;
    private final IJwtService _jwtService;
    private final IBackListService _blacklistService;
    private final KafkaProducer _authEventProducer;

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
        this._blacklistService = backListService;
        this._authEventProducer = authEventProducer;
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

            sendVerifyEmail(
                    createdUser.getId(),
                    createdUser.getEmail(),
                    createdUser.getPhoneNumber(),
                    createdUser.getFirstName(),
                    createdUser.getLastName(),
                    EmailTemplate.VERIFY_EMAIL,
                    ContactType.EMAIL
            );

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
    public ResendResponse resend(ResendRequest request) {
        _log.info("resend, contactType={}, purpose={}", request.getContactType(), request.getPurpose());

        switch (request.getContactType()) {
            case EMAIL -> resendToEmail(request);
            case PHONE -> resendToPhone(request);  // future
        }

        return ResendResponse.builder()
                .contactType(request.getContactType())
                .identifier(request.getIdentifier())
                .purpose(request.getPurpose())
                .build();
    }

    private void resendToEmail(ResendRequest request) {
        FoundUserResponse user;
        try {
            user = _userServiceRpcClient.findByEmail(request.getIdentifier());
        } catch (RpcException rpcEx) {
            _log.error("resendToEmail, RPC failed for email={}", request.getIdentifier(), rpcEx);
            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        // Security: always return true even if user not found
        if (!user.getFound() || !user.getIsActive() || user.getIsDeleted()) {
            _log.warn("resendToEmail, user not found or inactive, returning silently");
            return;
        }

        switch (request.getPurpose()) {
            case VERIFY, RESEND_VERIFY_EMAIL -> {
                if (user.getIsVerified()) {
                    _log.info("resendToEmail, already verified, skipping");
                    return;
                }
                resendVerifyEmail(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        ContactType.EMAIL
                );
            }
            case RESET_PASSWORD -> {
                // future: sendResetPasswordEmail(...)
                _log.info("resendToEmail, RESET_PASSWORD not yet implemented");
            }
        }
    }

    private void resendToPhone(ResendRequest request) {
        // future: OTP via SMS
        _log.info("resendToPhone, not yet implemented for identifier={}", request.getIdentifier());
    }

    @Override
    public VerifyAccountResponse verifyAccount(VerifyAccountRequest request) {
        _log.info("verifyAccountThroughEmail, type={}", request.getType());

        if (!_jwtService.isValidTokenIgnoreExpiry(request.getToken())) {
            _log.warn("verifyAccount, invalid token signature {}", request.getToken());
            throw new AppException(
                    "auth.verify.token.invalid",
                    Constant.RES3005,
                    HttpStatus.BAD_REQUEST.name()
            );
        }

        if (this._blacklistService.isBlacklisted(request.getToken())) {
            _log.warn("verifyAccount, token is blacklisted");
            throw new AppException(
                    "auth.verify.token_invalid",
                    Constant.RES3005,
                    HttpStatus.BAD_REQUEST.name()
            );
        }

        if (_jwtService.isTokenExpired(request.getToken())) {
            _log.info("verifyAccount, token expired, need to resend new verify url");

            // extract info from expired token to return to FE
            String email = _jwtService.extractClaimIgnoreExpiry(request.getToken(), "email", String.class);

            Map<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("contactType", request.getType().name());

            throw new AppException(
                    "auth.verify.token_expired",
                    Constant.RES3005,
                    HttpStatus.GONE.name(),
                    data
            );
        }

        String userId = _jwtService.extractSubject(request.getToken());

        FoundUserResponse user;

        try {
            user = _userServiceRpcClient.findById(userId);
        } catch (RpcException rpcEx) {
            _log.error("verifyAccount, User-service RPC failed. id={}",
                    userId, rpcEx);
            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        if(!user.getFound()){
            _log.error("verifyAccount, failed. User with id {} is not found", userId);
            throw new AppException(
                    "auth.verify.user_not_found",
                    Constant.RES3002,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        if (!user.getIsActive() || user.getIsDeleted()) {
            _log.error("verifyAccount, failed. Invalid token with userId {}", userId);
            throw new AppException(
                    "auth.verify.token_invalid",
                    Constant.RES3007,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        if (user.getIsVerified()) {
            _log.info("verifyAccount, account already verified for userId={}", userId);
            return VerifyAccountResponse.builder()
                    .type(ContactType.EMAIL)
                    .build();
        }

        _blacklistService.blacklistToken(request.getToken());

        VerifyAccountRpcResponse verifiedUser;
        VerifyAccountRpcRequest verifyAccountRpcRequest =
                VerifyAccountRpcRequest.newBuilder().setId(userId).build();
        try {
           verifiedUser = _userServiceRpcClient.verifyAccount(verifyAccountRpcRequest);
        } catch (RpcException rpcEx) {
            _log.error("verifyAccount, RPC failed marking verified for userId={}", userId, rpcEx);
            throw new AppException(
                    "user.service.rpc.error",
                    Constant.RES0007,
                    HttpStatus.SERVICE_UNAVAILABLE.name()
            );
        }

        if(verifiedUser.getUserNotFound()){
            _log.error("verifyAccount, failed. User with id {} is not found", userId);
            throw new AppException(
                    "auth.verify.user_not_found",
                    Constant.RES3002,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        _log.info("verifyAccount, successfully verified for userId={}", userId);

        return VerifyAccountResponse.builder()
                .type(ContactType.EMAIL)
                .email(verifiedUser.getEmail())
                .firstName(verifiedUser.getFirstName())
                .lastName(verifiedUser.getLastName())
                .phoneNumber(verifiedUser.getPhoneNumber())
                .verified(true)
                .isTokenExpired(false)
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
    public void signOut(String accessToken, String refreshToken) {
        _log.info("signOut, processing sign out request");

        // Blacklist access token
        _blacklistService.blacklistToken(accessToken);

        // Blacklist refresh token if provided
        if (refreshToken != null && !refreshToken.isBlank()) {
            _blacklistService.blacklistToken(refreshToken);
        }

        _log.info("signOut, tokens blacklisted successfully");
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

    private void sendVerifyEmail(
            String userId,
            String email,
            String phoneNumber,
            String firstName,
            String lastName,
            String templateName,
            ContactType contactType
    ) {
        FoundUserResponse user;
        try {
            user = _userServiceRpcClient.findById(userId);
        } catch (RpcException rpcEx) {
            _log.error("sendVerifyToken, RPC failed for userId={}", userId, rpcEx);
            throw new AppException("user.service.rpc.error", Constant.RES0007, HttpStatus.SERVICE_UNAVAILABLE.name());
        }

        if (!user.getFound() || !user.getIsActive() || user.getIsDeleted()) {
            throw new AppException("auth.verify.user_not_found", Constant.RES3002, HttpStatus.UNAUTHORIZED.name());
        }

        if (user.getIsVerified()) {
            _log.info("_resendVerifyToken, account already verified for userId={}", userId);
            return;
        }

        //Build verify token
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("phoneNumber", phoneNumber);
        claims.put("firstName", firstName);
        claims.put("lastName", lastName);
        claims.put("type", "VERIFY_EMAIL");
        claims.put("contactType", contactType);

        String verifyToken = _jwtService.generateVerifyAccountToken(
                userId, claims
        );

        String verifyUrl = UriComponentsBuilder
                .fromUriString("http://localhost:8081")  // "http://localhost:8081"
                .path("/api/v1/auth/verify")
                .queryParam("token", verifyToken)
                .queryParam("type", ContactType.EMAIL.name())
                .toUriString();

        // Build metadata map
        Map<String, Value> metadataFields = new HashMap<>();
        metadataFields.put("firstName", Value.newBuilder().setStringValue(firstName).build());
        metadataFields.put("lastName",  Value.newBuilder().setStringValue(lastName).build());
        metadataFields.put("verifyUrl", Value.newBuilder().setStringValue(verifyUrl).build());

        Struct metadata = Struct.newBuilder()
                .putAllFields(metadataFields)
                .build();

        SendEmailEvent emailEvent = SendEmailEvent.newBuilder()
                .setRecipient(email)
                .setUserId(userId)
                .setRecipientId(userId)
                .addTo(email)
                .setSubject("Please verify your account!")
                .setMetadata(metadata)
                .setFrom("no-reply@restaurant.com")
                .setType(NotiType.SYSTEM.name())
                .setTemplateName(templateName)
                .build();                                  // userId is optional — set only if available

        _log.info("signUp, about to send Kafka event for email: {}", email);
        _authEventProducer.pushUserCreatedEvent(userId , emailEvent, new HashMap<>() );
        _log.info("signUp, Kafka event dispatched for email: {}", email);
    }

    private void resendVerifyEmail(
            String userId,
            String email,
            String firstName,
            String lastName,
            ContactType contactType
    ) {
        // Token generation is verify-specific — belongs here
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("firstName", firstName != null ? firstName : "");
        claims.put("lastName", lastName != null ? lastName : "");
        claims.put("purpose", NotificationPurpose.RESEND_VERIFY_EMAIL);

        String verifyToken = _jwtService.generateVerifyAccountToken(userId, claims);

        String verifyUrl = UriComponentsBuilder
                .fromUriString("http://localhost:8081")
                .path("/api/v1/auth/verify")
                .queryParam("token", verifyToken)
                .queryParam("type", ContactType.EMAIL.name())
                .toUriString();

        Map<String, Value> metadataFields = new HashMap<>();
        metadataFields.put("firstName", Value.newBuilder()
                .setStringValue(firstName != null ? firstName : "").build());
        metadataFields.put("lastName", Value.newBuilder()
                .setStringValue(lastName != null ? lastName : "").build());
        metadataFields.put("verifyUrl", Value.newBuilder()
                .setStringValue(verifyUrl).build());

        Struct metadata = Struct.newBuilder()
                .putAllFields(metadataFields)
                .build();

/*        String templateName = switch (purpose) {
            case VERIFY             -> EmailTemplate.VERIFY_EMAIL;
            case RESEND_VERIFY_EMAIL -> EmailTemplate.RESEND_VERIFY_EMAIL;
            default -> throw new IllegalArgumentException("Unsupported purpose: " + purpose);
        };*/

        ResendExternalNotificationEvent event = ResendExternalNotificationEvent.newBuilder()
                .setRecipient(email != null ? email : "")
                .setRecipientId(userId)
                .setUserId(userId)
                .setContactType(contactType.name())
                .setPurpose(NotificationPurpose.RESEND_VERIFY_EMAIL.name())
                .addTo(email != null ? email : "")
                .setSubject(getEmailSubject(NotificationPurpose.RESEND_VERIFY_EMAIL))
                .setFrom("no-reply@restaurant.com")
                .setTemplateName(EmailTemplate.RESEND_VERIFY_EMAIL)
                .setMetadata(metadata)
                .setType(NotiType.SYSTEM.name())
                .build();

        pushResendExternalNotificationEvent(event);
    }

    private void pushResendExternalNotificationEvent(
            ResendExternalNotificationEvent event
    ) {
        _log.info("pushResendExternalNotificationEvent, userId={}, contactType={}, purpose={}",
                event.getUserId(), event.getContactType(), event.getPurpose());

        String eventId = UUID.randomUUID().toString();
        Map<String, String> headers = new HashMap<>();
        headers.put(Constant.H_EVENT_ID, eventId);

        _authEventProducer.pushResendExternalNotificationEvent(event.getUserId(), event, headers);
        _log.info("sendExternalVerifyNotification, event pushed userId={}", event.getUserId());
    }

    private String getEmailSubject(NotificationPurpose purpose) {
        return switch (purpose) {
            case VERIFY         -> "Please verify your account!";
            case RESEND_VERIFY_EMAIL  -> "New verification link";
            case RESET_PASSWORD -> "Reset your password";
        };
    }
}
