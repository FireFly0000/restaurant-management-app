package com.restaurant.userservice.core.controller.v1;

import com.restaurant.userservice.core.service.user.IUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final IUserService _userService;

    public UserController(
            IUserService userService
    ){
        this._userService = userService;
    }
}
