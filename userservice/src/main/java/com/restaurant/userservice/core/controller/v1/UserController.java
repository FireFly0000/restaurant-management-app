package com.restaurant.userservice.core.controller.v1;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.exception.AppException;
import com.restaurant.commons.utils.AppUtils;
import com.restaurant.userservice.core.service.user.IUserService;
import com.restaurant.userservice.core.service.user.dto.UpdatePhoneNumberRequest;
import com.restaurant.userservice.core.service.user.dto.UpdatePhoneNumberResponse;
import com.restaurant.userservice.core.service.user.dto.UpdateUserInfoRequest;
import com.restaurant.userservice.core.service.user.dto.UpdateUserInfoResponse;
import com.restaurant.userservice.utils.Utils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PatchMapping("/{userId}/info")
    public ResponseEntity<?> updateUserInfo(
            @PathVariable String userId,
            @RequestBody UpdateUserInfoRequest request,
            @RequestHeader("X-User-Id") String requestingUserId
    ) {
        _log.info("updateUserInfo, request received for userId={}", userId);

        if (!userId.equals(requestingUserId)) {
            throw new AppException(
                    Utils.getMessage("user.update_info.forbidden"),
                    Constant.RES3011,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        UpdateUserInfoResponse result = _userService.updateUserInfo(UUID.fromString(userId), request);

        return new ResponseEntity<>(
                AppUtils.buildResponse(
                        Utils.getMessage("user.update_info.success"),
                        result,
                        null
                ),
                HttpStatus.OK
        );
    }

    @PatchMapping("/{userId}/phone")
    public ResponseEntity<?> updatePhoneNumber(
            @PathVariable String userId,
            @RequestBody @Valid UpdatePhoneNumberRequest request,
            @RequestHeader("X-User-Id") String requestingUserId
    ) {
        _log.info("updatePhoneNumber, request received for userId={}", userId);

        if (!userId.equals(requestingUserId)) {
            throw new AppException(
                    Utils.getMessage("user.update_info.forbidden"),
                    Constant.RES3011,
                    HttpStatus.FORBIDDEN.name()
            );
        }

        UpdatePhoneNumberResponse result = _userService.updatePhoneNumber(
                UUID.fromString(userId), request
        );

        return new ResponseEntity<>(
                AppUtils.buildResponse(
                        Utils.getMessage("user.update_phone.success"),
                        result,
                        null
                ),
                HttpStatus.OK
        );
    }
}
