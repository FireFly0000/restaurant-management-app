package com.restaurant.authservice.core.controller.v1;

import com.restaurant.authservice.core.service.auth.IAuthService;
import com.restaurant.authservice.core.service.auth.dto.VerifyAccountRequest;
import com.restaurant.authservice.core.service.auth.dto.VerifyAccountResponse;
import com.restaurant.authservice.core.service.auth.dto.*;
import com.restaurant.authservice.utils.Utils;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.exception.AppException;
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
    public ResponseEntity<?> signUp(@RequestBody @Valid SignupRequest request) {
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

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@Valid @ModelAttribute VerifyAccountRequest request) {
        _log.info("verifyContact, request received for type={}", request.getType());

        String successMsg = Utils.getMessage("auth.verify.success");
        String expiredMsg = Utils.getMessage("auth.verify.token_expired");
        VerifyAccountResponse result = _authService.verifyAccount(request);

        if(result.isTokenExpired()){
            return new ResponseEntity<>(
                    AppUtils.buildResponse(
                            expiredMsg,
                            result,
                            null
                    ),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                AppUtils.buildResponse(
                        successMsg,
                        result,
                        null
                ),
                HttpStatus.OK
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

        if (refreshToken == null) {
            throw new AppException(
                    "auth.refresh.token_missing",
                    Constant.RES3008,
                    HttpStatus.UNAUTHORIZED.name()
            );
        }

        AuthResponse result = _authService.refreshToken(refreshToken);
        return new ResponseEntity<>(
                AppUtils.buildResponse(Utils.getMessage("auth.refresh.success"), result, null),
                HttpStatus.OK
        );
    }
}
