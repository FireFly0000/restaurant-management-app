package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.core.repository.IBusinessLocationSettingRepository;
import com.restaurant.businessservice.model.LocationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BusinessLocationSettingServiceImpl implements  IBusinessLocationSettingService {

    private static final Logger _log = LoggerFactory.getLogger(BusinessLocationSettingServiceImpl.class);

    private final IBusinessLocationSettingRepository _repo;

    public BusinessLocationSettingServiceImpl(
            IBusinessLocationSettingRepository repo
    ) {
        this._repo = repo;
    }

    @Override
    public boolean existsOneLocationSettingInLocation(UUID locationId) {
        return  _repo.existsByLocationId(locationId);
    }

    @Override
    public LocationSetting save(LocationSetting entity) {
        return _repo.save(entity);
    }
}
