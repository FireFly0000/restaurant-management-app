package com.restaurant.authservice.core.service.auth;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.restaurant.authservice.core.kafka.KafkaProducer;
import com.restaurant.authservice.core.rpc.IUserServiceRpcClient;
import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.enums.NotiType;
import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import com.restaurant.commons.core.rpc.user.CreateUserResponse;
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
    private final KafkaProducer _authEventProducer;

    public AuthServiceImpl(
            IUserServiceRpcClient userServiceRpcClient,
            PasswordEncoder passwordEncoder,
            KafkaProducer authEventProducer
    ){
        this._userServiceRpcClient = userServiceRpcClient;
        this._passwordEncoder = passwordEncoder;
        this._authEventProducer = authEventProducer;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public Boolean signUp(RegisterRequest request) {
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

            if (!createdUser.getSuccess()) {
                _log.error("User creation failed in user-service for email: {}", request.getEmail());
                throw new AppException(
                        "user.created.false",
                        Constant.RES3008,
                        HttpStatus.INTERNAL_SERVER_ERROR.name()
                );
            }
            _log.info("Registration successful for email: {}", request.getEmail());

            // Build metadata map
            Map<String, Value> metadataFields = new HashMap<>();
            metadataFields.put("firstName", Value.newBuilder().setStringValue(request.getFirstName()).build());
            metadataFields.put("lastName",  Value.newBuilder().setStringValue(request.getLastName()).build());
            metadataFields.put("verifyUrl", Value.newBuilder().setStringValue("https://yourapp.com/verify?token=").build());

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
