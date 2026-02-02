package com.restaurant.authservice.core.controller.v1;

import com.restaurant.authservice.core.service.auth.IAuthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IAuthService _authService;

    public AuthController(
            IAuthService authService
    ){
        this._authService = authService;
    }
}
