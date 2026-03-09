package com.restaurant.authservice.core.controller.v1;

import com.restaurant.authservice.core.service.auth.IAuthService;
import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.authservice.utils.Utils;
import com.restaurant.commons.utils.AppUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        SignupResponse result = _authService.signUp(request);

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

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid LoginRequest request) {
        _log.info("signIn, Sign in for user with email: {}", request.getEmail());

        String successMsg = Utils.getMessage("auth.signin.success");
        AuthResponse result = _authService.signIn(request);

        return new ResponseEntity<>(
                AppUtils.buildResponse(successMsg, result, null),
                HttpStatus.OK
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken
    ) {
        _log.info("refreshToken, request received");

        AuthResponse result = _authService.refreshToken(refreshToken);
        return new ResponseEntity<>(
                AppUtils.buildResponse(Utils.getMessage("auth.refresh.success"), result, null),
                HttpStatus.OK
        );
    }
}
