package com.restaurant.businessservice.core.service.business;

import com.restaurant.businessservice.core.service.business.dto.CreateBusinessRequest;
import com.restaurant.businessservice.model.Business;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IBusinessService {
    Business findById(UUID id);
    Business getById(UUID id);
    Business getByIdAndThrow(UUID id);

    Business create(CreateBusinessRequest request);

    Business save(Business entity);
    List<Business> saveAll(Collection<Business> entities);
}
