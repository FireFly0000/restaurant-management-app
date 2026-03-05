package com.restaurant.businessservice.core.controller.v1;

import com.restaurant.businessservice.common.MapperUtils;
import com.restaurant.businessservice.common.MsgUtil;
import com.restaurant.businessservice.core.service.business.IBusinessService;
import com.restaurant.businessservice.core.service.business.dto.BusinessResponse;
import com.restaurant.businessservice.core.service.business.dto.CreateBusinessRequest;
import com.restaurant.businessservice.core.service.business.dto.UpdateBusinessRequest;
import com.restaurant.businessservice.core.service.business.dto.UploadFileRequest;
import com.restaurant.businessservice.model.Business;
import com.restaurant.commons.core.dtos.ApiResponse;
import com.restaurant.commons.utils.AppUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {
    private final IBusinessService _businessService;
    private final MsgUtil _msgUtil;

    public BusinessController(
        IBusinessService _businessService,
        MsgUtil _msgUtil
    ){
        this._businessService = _businessService;
        this._msgUtil = _msgUtil;
    }

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@_authorizer.isLogedIn()")
    public ResponseEntity<?> create(@RequestBody @Valid CreateBusinessRequest request){
        Business business = this._businessService.create(request);
        BusinessResponse response = MapperUtils.getBusinessResponse(business, new HashMap<>());
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("business.create.success"),response, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> update(@PathVariable("id") String id,@RequestBody @Valid UpdateBusinessRequest request){
        UUID businessId = UUID.fromString(id);
        Business business = this._businessService.update(businessId, request);
        BusinessResponse response = MapperUtils.getBusinessResponse(business, new HashMap<>());
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("business.update.success"),response, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/active", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> active(@PathVariable("id") String id){
        UUID businessId = UUID.fromString(id);
        Boolean result = this._businessService.active(businessId);
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("business.active.success"),result, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/inactive", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> inactive(@PathVariable("id") String id){
        UUID businessId = UUID.fromString(id);
        Boolean result = this._businessService.inactive(businessId);
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("business.inactive.success"),result, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/cover-image", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> updateCoverImage(@PathVariable("id") String id, @RequestBody @Valid UploadFileRequest request){
        UUID businessId = UUID.fromString(id);
        String result = this._businessService.updateCoverImage(businessId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("url", result);

        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("business.update_cover_img.success"),response, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/avatar", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> avatar(@PathVariable("id") String id, @RequestBody @Valid UploadFileRequest request){
        UUID businessId = UUID.fromString(id);
        String result = this._businessService.updateAvatar(businessId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("url", result);

        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("business.update_avatar.success"),response, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
