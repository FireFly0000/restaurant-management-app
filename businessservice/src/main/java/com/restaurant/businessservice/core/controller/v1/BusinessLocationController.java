package com.restaurant.businessservice.core.controller.v1;

import com.restaurant.businessservice.common.MapperUtils;
import com.restaurant.businessservice.common.MsgUtil;
import com.restaurant.businessservice.core.service.location.IBusinessLocationService;
import com.restaurant.businessservice.core.service.location.dto.BusinessLocationResponse;
import com.restaurant.businessservice.core.service.location.dto.CreateLocationRequest;
import com.restaurant.businessservice.core.service.location.dto.UpdateLocationManagerRequest;
import com.restaurant.businessservice.core.service.location.dto.UpdateLocationRequest;
import com.restaurant.businessservice.model.Location;
import com.restaurant.commons.core.dtos.ApiResponse;
import com.restaurant.commons.utils.AppUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/business-locations")
public class BusinessLocationController {

    private final IBusinessLocationService _locationService;
    private final MsgUtil _msgUtil;

    public BusinessLocationController(
            IBusinessLocationService _locationService,
            MsgUtil _msgUtil
    ){
        this._locationService = _locationService;
        this._msgUtil = _msgUtil;
    }

    @PostMapping(value = "/", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#request.businessId, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> create(@RequestBody @Valid CreateLocationRequest request){
        Location location = this._locationService.create(request);
        BusinessLocationResponse response = MapperUtils.getLocationResponse(location, new HashMap<>());
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("location.create.success"), response, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody @Valid UpdateLocationRequest request){
        UUID locationId = UUID.fromString(id);
        Location location = this._locationService.update(locationId, request);
        BusinessLocationResponse response = MapperUtils.getLocationResponse(location, new HashMap<>());
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("location.update.success"), response, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/active", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> active(@PathVariable("id") String id){
        UUID locationId = UUID.fromString(id);
        Boolean result = this._locationService.active(locationId);
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("location.active.success"), result, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/inactive", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#id, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> inactive(@PathVariable("id") String id){
        UUID locationId = UUID.fromString(id);
        Boolean result = this._locationService.inactive(locationId);
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("location.inactive.success"), result, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/manager", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@_authorizer.isOwner(#request.businessId, T(com.restaurant.commons.constant.Entity).BUSINESS)")
    public ResponseEntity<?> changeManager(@PathVariable("id") String id, @RequestBody @Valid UpdateLocationManagerRequest request){
        UUID locationId = UUID.fromString(id);
        Boolean result = this._locationService.changeManager(locationId, request);
        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("location.change_manager.success"), result, null);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
