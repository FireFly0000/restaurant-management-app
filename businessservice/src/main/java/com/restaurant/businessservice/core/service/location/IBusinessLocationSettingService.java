package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.model.LocationSetting;

import java.util.UUID;

public interface IBusinessLocationSettingService {
    boolean existsOneLocationSettingInLocation(UUID locationId);

    LocationSetting save(LocationSetting entity);
}
