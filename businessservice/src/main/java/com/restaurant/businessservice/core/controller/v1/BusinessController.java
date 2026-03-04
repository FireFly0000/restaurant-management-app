package com.restaurant.businessservice.core.controller.v1;

import com.restaurant.businessservice.common.MapperUtils;
import com.restaurant.businessservice.core.service.business.IBusinessService;
import com.restaurant.businessservice.core.service.business.dto.BusinessResponse;
import com.restaurant.businessservice.core.service.business.dto.CreateBusinessRequest;
import com.restaurant.businessservice.model.Business;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {
    private final IBusinessService _businessService;

    public BusinessController(
        IBusinessService _businessService
    ){
        this._businessService = _businessService;
    }

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody @Valid CreateBusinessRequest request){
        Business business = this._businessService.create(request);
        BusinessResponse response = MapperUtils.getBusinessResponse(business, new HashMap<>());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
