package com.restaurant.businessservice.core.service.business;

import com.restaurant.businessservice.core.service.business.dto.BusinessResponse;
import com.restaurant.businessservice.core.service.business.dto.CreateBusinessRequest;
import com.restaurant.businessservice.core.service.business.dto.UpdateBusinessRequest;
import com.restaurant.businessservice.core.service.business.dto.UploadFileRequest;
import com.restaurant.businessservice.model.Business;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IBusinessService {
    Business findById(UUID id);
    Business getById(UUID id);
    Business getByIdAndThrow(UUID id);
    Boolean existByIdAndOwnerId(UUID id, UUID ownerId);

    Business create(CreateBusinessRequest request);
    Business update(UUID id, UpdateBusinessRequest request);
    Boolean active(UUID id);
    Boolean inactive(UUID id);
    String updateCoverImage(UUID id, UploadFileRequest request);
    String updateAvatar(UUID id, UploadFileRequest request);

    BusinessResponse getListBusinesses();
    BusinessResponse getBusinessById(UUID id);

    Business save(Business entity);
    List<Business> saveAll(Collection<Business> entities);
}
