package com.restaurant.userservice.core.rpc;

import com.restaurant.commons.core.enums.UserType;
import com.restaurant.userservice.core.service.user.IUserService;
import com.restaurant.commons.core.rpc.user.*;
import com.restaurant.userservice.model.User;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@DubboService(
        version = "1.0.0",
        group = "user-service",
        protocol = "tri"  // Triple protocol
)
@Service
@RequiredArgsConstructor
public class UserServiceRpcImpl extends DubboUserServiceTriple.UserServiceImplBase {

    private final IUserService userService;
    private final Logger _log = LoggerFactory.getLogger(UserServiceRpcImpl.class);

    @Override
    public ExistsResponse existsByEmail(ExistsByEmailRequest request) {
        try {
            _log.info("RPC: existsByEmail called with email={}", request.getEmail());
            boolean exists = userService.existsByEmail(request.getEmail());
            return ExistsResponse.newBuilder()
                    .setExists(exists)
                    .build();
        } catch (Exception e){
            _log.error("RPC: checked email exists failed", e);
            throw Status.UNKNOWN.withDescription(e.getMessage()).asRuntimeException();
        }
    }

    @Override
    public ExistsResponse existsByPhoneNumber(ExistsByPhoneNumberRequest request) {
        try {
            _log.info("RPC: existsByPhoneNumber called with phone number={}", request.getPhoneNumber());
            boolean exists = userService.existsByPhoneNumber(request.getPhoneNumber());
            return ExistsResponse.newBuilder()
                    .setExists(exists)
                    .build();
        } catch (Exception e){
            _log.error("RPC: checked phone number exists failed", e);
            throw Status.UNKNOWN.withDescription(e.getMessage()).asRuntimeException();
        }
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        try {
            User saved = userService.save(buildUserFromRequest(request));

            return CreateUserResponse.newBuilder()
                    .setId(saved.getId().toString())
                    .setSuccess(true)
                    .setMessage("User created successfully")
                    .build();

        } catch (Exception e) {
            _log.error("RPC: createUser failed", e);
            throw Status.UNKNOWN.withDescription(e.getMessage()).asRuntimeException();
        }
    }

    @Override
    public GetUsersByIdsResponse getUsersByIds(GetUsersByIdsRequest request) {
        try{
            _log.info("getUsersByIds, Start get users by Ids");
            List<UUID> ids = request.getIdsList().stream().map(UUID::fromString).toList();
            List<User> users = userService.getUsersByIds(ids);
            List<UserRpcResponse> usersReponse = users.stream().map(this::buildUserRpcResponse).toList();

            return GetUsersByIdsResponse.newBuilder()
                    .addAllUsers(usersReponse)
                    .build();

        }catch (Exception e){
            _log.error("getUsersByIds, get users failed: {}", e.getMessage());
            throw Status.UNKNOWN.withDescription(e.getMessage()).asRuntimeException();
        }
    }

    private User buildUserFromRequest(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatarUrl(request.getAvatarUrl().isEmpty() ? null : request.getAvatarUrl());
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setUserType(UserType.CUSTOMER);
        return user;
    }

    private UserRpcResponse buildUserRpcResponse(User user) {
        return UserRpcResponse.newBuilder()
                .setId(user.getId().toString())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setAvatarUrl(user.getAvatarUrl())
                .setBirthDate(user.getBirhtDate().toString())
                .setPhoneNumber(user.getPhoneNumber())
                .setUserType(user.getUserType().name())
                .build();
    }
}