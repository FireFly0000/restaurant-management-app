package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.core.service.location.dto.CreateLocationRequest;
import com.restaurant.businessservice.core.service.location.dto.UpdateLocationManagerRequest;
import com.restaurant.businessservice.core.service.location.dto.UpdateLocationRequest;
import com.restaurant.businessservice.model.Location;

import java.util.List;
import java.util.UUID;

public interface IBusinessLocationService {
    Location getByIdAndThrow(UUID id);
    Location findById(UUID id);
    Location getById(UUID id);

    Location create(CreateLocationRequest request);
    Location update(UUID id, UpdateLocationRequest request);
    Boolean active(UUID id);
    Boolean inactive(UUID id);
    Boolean changeManager(UUID id, UpdateLocationManagerRequest request);

    Location save(Location entity);

    List<Location> getBusinessLocationsByBusinessId(UUID businessId);
    List<Location> getBusinessLocationsByBusinessId(UUID businessId, Boolean isActive);
}
