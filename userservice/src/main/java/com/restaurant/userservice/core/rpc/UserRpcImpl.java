package com.restaurant.userservice.core.rpc;

import com.restaurant.userservice.core.service.user.IUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@DubboService(protocol = "tri")
@Component
public class UserRpcImpl extends BaseRpcService implements IUserRpcService {

    private final IUserService userService;

    public UserRpcImpl(IUserService userService){
        this.userService = userService;
    }

    @Override
    public boolean existsByEmail(String email) {
        return execute(
                () -> userService.existsByEmail(email),
                "existsByEmail"
        );
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return execute(
                () -> userService.existsByPhoneNumber(phoneNumber),
                "existsByPhoneNumber"
        );
    }
}