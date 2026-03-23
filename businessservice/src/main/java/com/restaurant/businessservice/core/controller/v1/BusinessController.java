package com.restaurant.businessservice.core.controller.v1;

import com.restaurant.businessservice.common.MapperUtils;
import com.restaurant.businessservice.common.MsgUtil;
import com.restaurant.businessservice.core.service.business.IBusinessService;
import com.restaurant.businessservice.core.service.business.dto.*;
import com.restaurant.businessservice.model.Business;
import com.restaurant.commons.core.dtos.ApiResponse;
import com.restaurant.commons.core.dtos.Pagination;
import com.restaurant.commons.utils.AppUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PostMapping(value = "/", produces = "application/json")
    @PreAuthorize("@_authorizer.isLogedIn()")
    public ResponseEntity<?> create(@ModelAttribute @Valid CreateBusinessRequest request){
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

    @GetMapping(value = "/", produces = "application/json")
    @PreAuthorize("@_authorizer.isLogedIn()")
    public ResponseEntity<?> getListBusiness(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "30") Integer limit,
            @RequestParam(value = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(value = "sort", defaultValue = "DESC") String sort,
            @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
            @RequestParam(value = "is_actived", required = false) Boolean isActived,
            @RequestParam(value = "search", required = false) String search
    ){
        try{
            Sort.Direction.fromString(sort);
        }catch (Exception ex){
            sort = "DESC";
        }

        BusinessFilter filter = new BusinessFilter();
        filter.setPage(page);
        filter.setLimit(limit);
        filter.setSortBy(sortBy);
        filter.setIsDeleted(isDeleted);
        filter.setIsActived(isActived);
        filter.setSort(sort);
        filter.setSearch(search);

        Page<Business> pageBusiness = _businessService.getListBusinesses(filter);

        Pagination pagination = Pagination.builder()
                .hasNext(pageBusiness.hasNext())
                .hasPrevious(pageBusiness.hasPrevious())
                .totalItems(pageBusiness.getTotalElements())
                .totalPages(pageBusiness.getTotalPages())
                .limit(filter.getLimit())
                .page(filter.getPage())
                .build();

        List<BusinessResponse> response = pageBusiness.getContent().stream().map(b -> MapperUtils.getBusinessResponse(b, new HashMap<>())).collect(Collectors.toList());

        ApiResponse apiResponse = AppUtils.buildResponse(_msgUtil.getMessage("business.success"),response, null, pagination);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
