package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.core.service.location.dto.BusinessLocationResponse;
import com.restaurant.businessservice.model.Location;

import java.util.List;
import java.util.UUID;

public interface IBusinessLocationService {
    List<Location> getBusinessLocationsByBusinessId(UUID businessId);
}
