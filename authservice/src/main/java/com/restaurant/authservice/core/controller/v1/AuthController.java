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

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> testRegister(@RequestBody RegisterRequest request) {
        _log.info("Testing registration for email: {}", request.getEmail());

        Map<String, Object> response = new HashMap<>();

        try {
            Boolean result = _authService.register(request);

            if (result) {
                response.put("success", true);
                response.put("message", "Email is available for registration");
                response.put("email", request.getEmail());
                response.put("rpcCheckPassed", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Email already exists");
                response.put("email", request.getEmail());
                response.put("rpcCheckPassed", false);
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            _log.error("Registration test failed", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("rpcCheckPassed", false);
            return ResponseEntity.status(500).body(response);
        }
    }
}
