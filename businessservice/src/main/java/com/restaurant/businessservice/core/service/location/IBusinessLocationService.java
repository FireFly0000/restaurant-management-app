package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.core.service.location.dto.CreateLocationRequest;
import com.restaurant.businessservice.model.Location;

import java.util.List;
import java.util.UUID;

public interface IBusinessLocationService {
    Location getByIdAndThrow(UUID id);
    Location findById(UUID id);
    Location getById(UUID id);

    Location create(CreateLocationRequest request);
    Location save(Location entity);

    List<Location> getBusinessLocationsByBusinessId(UUID businessId);
}
