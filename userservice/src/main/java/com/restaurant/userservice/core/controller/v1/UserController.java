package com.restaurant.userservice.core.controller.v1;

import com.restaurant.userservice.core.service.user.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final IUserService _userService;
    private final Logger _log = LoggerFactory.getLogger(UserController.class);

    public UserController(
            IUserService userService
    ){
        this._userService = userService;
    }

    /**
     * Test endpoint to check if email exists
     * GET /api/test/user/exists-by-email?email=test@example.com
     */
    @GetMapping("/exists-by-email")
    public ResponseEntity<Map<String, Object>> existsByEmail(@RequestParam String email) {
        _log.info("Checking if email exists: {}", email);

        try {
            boolean exists = _userService.existsByEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            response.put("email", email);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            _log.error("Error checking if email exists", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
