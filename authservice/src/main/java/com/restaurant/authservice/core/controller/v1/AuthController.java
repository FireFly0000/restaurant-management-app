package com.restaurant.authservice.core.controller.v1;

import com.restaurant.authservice.core.service.auth.IAuthService;
import com.restaurant.authservice.core.service.auth.dto.RegisterRequest;
import com.restaurant.authservice.utils.Utils;
import com.restaurant.commons.utils.AppUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IAuthService _authService;
    private final Logger _log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(
            IAuthService authService
    ){
        this._authService = authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid RegisterRequest request) {
        _log.info("signUp, Signup for user with email: {}", request.getEmail());

        String successMsg = Utils.getMessage("user.created.true");
        Boolean result = _authService.signUp(request);

        return new ResponseEntity<>
                (
                    AppUtils.buildResponse(
                        successMsg,
                        result,
                        null
                    ),
                    HttpStatus.CREATED
                );
    }
}
