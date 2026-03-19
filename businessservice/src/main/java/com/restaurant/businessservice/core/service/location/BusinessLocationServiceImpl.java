package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.core.repository.IBusinessLocationRepository;
import com.restaurant.businessservice.core.service.location.dto.BusinessLocationResponse;
import com.restaurant.businessservice.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BusinessLocationServiceImpl implements IBusinessLocationService{

    private static final Logger _log = LoggerFactory.getLogger(BusinessLocationServiceImpl.class);

    private final IBusinessLocationRepository _repo;

    public BusinessLocationServiceImpl(
            IBusinessLocationRepository _repo
    ){
        this._repo = _repo;
    }

    @Override
    public List<Location> getBusinessLocationsByBusinessId(UUID businessId) {
        _log.info("getBusinessLocationsByBusinessId, Start get locations of business with id = {}", businessId);
        if(businessId == null){
            _log.warn("getBusinessLocationsByBusinessId, businessId is null");
            return new ArrayList<>();
        }
        return this._repo.getBusinessLocationByBusinessId(businessId);
    }
}
