package com.restaurant.userservice.core.rpc;

import com.restaurant.userservice.core.service.user.IUserService;
import com.restaurant.commons.core.rpc.user.*;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        _log.info("RPC: existsByEmail called with email={}", request.getEmail());
        boolean exists = userService.existsByEmail(request.getEmail());
        return ExistsResponse.newBuilder()
                .setExists(exists)
                .build();
    }

    @Override
    public ExistsResponse existsByPhoneNumber(ExistsByPhoneNumberRequest request) {
        _log.info("RPC: existsByPhoneNumber called with phone number={}", request.getPhoneNumber());
        boolean exists = userService.existsByPhoneNumber(request.getPhoneNumber());
        return ExistsResponse.newBuilder()
                .setExists(exists)
                .build();
    }
}