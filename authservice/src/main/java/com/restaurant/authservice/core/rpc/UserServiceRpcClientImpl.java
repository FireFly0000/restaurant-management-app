package com.restaurant.authservice.core.rpc;

import com.restaurant.commons.core.rpc.user.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceRpcClientImpl implements IUserServiceRpcClient {

    @DubboReference(
            version = "1.0.0",
            group = "user-service",
            protocol = "tri",
            timeout = 3000,
            retries = 2,
            check = false
    )
    private UserService _userServiceStub;
    private final Logger _log = LoggerFactory.getLogger(UserServiceRpcClientImpl.class);

    /**
     * Check if user exists by email
     */
    @Override
    public boolean existsByEmail(String email) {
        _log.info("Calling user-service via Dubbo: existsByEmail({})", email);
        ExistsByEmailRequest request = ExistsByEmailRequest.newBuilder()
                .setEmail(email)
                .build();

        ExistsResponse response = _userServiceStub.existsByEmail(request);
        return response.getExists();
    }

    /**
     * Check if user exists by phone number
     */
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        _log.info("Calling user-service via Dubbo: existsByPhoneNumber({})", phoneNumber);
        ExistsByPhoneNumberRequest request = ExistsByPhoneNumberRequest.newBuilder()
                .setPhoneNumber(phoneNumber)
                .build();

        ExistsResponse response = _userServiceStub.existsByPhoneNumber(request);
        return response.getExists();
    }

    @Override
    public CreateUserResponse createUser(
        CreateUserRequest request
    ){
        _log.info("Calling user-service via Dubbo: createUser({})", request.getEmail());
        return _userServiceStub.createUser(request);
    }

    @Override
    public FoundUserResponse findByEmail( String email ){
        _log.info("Calling user-service via Dubbo: findByEmail({})", email);

        FindByEmailRequest request = FindByEmailRequest.newBuilder()
                .setEmail(email)
                .build();

        return _userServiceStub.findByEmail(request);
    }

    @Override
    public FoundUserResponse findById( String id ){
        _log.info("Calling user-service via Dubbo: findById({})", id);

        FindByIdRequest request = FindByIdRequest.newBuilder()
                .setId(id)
                .build();

        return _userServiceStub.findById(request);
    }

    @Override
    public VerifyAccountRpcResponse verifyAccount(VerifyAccountRpcRequest request){
        _log.info("Calling user-service via Dubbo: verifyAccount({})", request.getId());
        return _userServiceStub.verifyAccount(request);
    }

    @Override
    public UpdatePasswordRpcResponse updatePassword(UpdatePasswordRpcRequest request){
        _log.info("Calling user-service via Dubbo: updatePassword({})", request.getId());
        return  _userServiceStub.updatePassword(request);
    }
}