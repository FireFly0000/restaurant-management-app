package com.restaurant.authservice.core.controller.v1;

import com.restaurant.authservice.core.service.auth.IAuthService;
import com.restaurant.authservice.core.service.auth.dto.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody RegisterRequest request) {
        _log.info("Signup for user with email: {}", request.getEmail());

        Map<String, Object> response = new HashMap<>();

        try {
            Boolean result = _authService.register(request);
            response.put("success", true);
            response.put("message", "Registration successful");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());  // "Email already exists" etc.
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            _log.error("Registration failed", e);
            response.put("success", false);
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }
}
