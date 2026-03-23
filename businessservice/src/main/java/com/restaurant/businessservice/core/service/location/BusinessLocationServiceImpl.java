package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.common.MsgUtil;
import com.restaurant.businessservice.core.repository.IBusinessLocationRepository;
import com.restaurant.businessservice.core.service.business.IBusinessService;
import com.restaurant.businessservice.core.service.location.dto.CreateLocationRequest;
import com.restaurant.businessservice.core.service.location.dto.CreateLocationSettingRequest;
import com.restaurant.businessservice.model.Location;
import com.restaurant.businessservice.model.LocationSetting;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BusinessLocationServiceImpl implements IBusinessLocationService{

    private static final Logger _log = LoggerFactory.getLogger(BusinessLocationServiceImpl.class);

    private final IBusinessLocationRepository _repo;
    private final MsgUtil _msgUtil;
    private final IBusinessLocationSettingService _settingService;

    public BusinessLocationServiceImpl(
            IBusinessLocationRepository _repo,
            MsgUtil _msgUtil,
            IBusinessLocationSettingService _settingService
    ){
        this._repo = _repo;
        this._msgUtil = _msgUtil;
        this._settingService = _settingService;
    }

    @Override
    public Location getByIdAndThrow(UUID id) {
        Optional<Location> location = _repo.getLocationById(id);
        if(location.isEmpty()){
            _log.warn("findById, Location not found with id = {}", id);
            throw new AppException(_msgUtil.getMessage("location.resource.not_found", id), Constant.RES3001, "400");
        }
        _log.info("findById, Location with id = {} found", id);
        return location.get();
    }

    @Override
    public Location findById(UUID id) {
        Optional<Location> location = _repo.findById(id);
        if(location.isEmpty()){
            _log.warn("findById, Location not found with id = {}", id);
            return null;
        }
        _log.info("findById, Location with id = {} found", id);
        return location.get();
    }

    @Override
    public Location getById(UUID id) {
        Optional<Location> location = _repo.getLocationById(id);
        if(location.isEmpty()){
            _log.warn("findById, Location not found with id = {}", id);
            return null;
        }
        _log.info("findById, Location with id = {} found", id);
        return location.get();
    }

    @Override
    @Transactional
    public Location create(CreateLocationRequest request) {
        _log.info("create, Start create location from Business = {}", request.getBusinessId());
        Location location = Location.builder()
                .businessId(UUID.fromString(request.getBusinessId()))
                .branchName(request.getBranchName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .startDate(request.getStartDate())
                .address(request.getAddress())
                .build();

        // Save to get ID
        Location locationSaved = this.save(location);
        _log.debug("create, Saved location id = {}", locationSaved.getId());

        CreateLocationSettingRequest lsRequest = request.getLocationSetting();
        LocationSetting locationSetting = LocationSetting.builder()
                .locationId(locationSaved.getId())
                .settingName(lsRequest.getSettingName())
                .openingHours(lsRequest.getOpeningHours())
                .hasParking(lsRequest.getHasParking())
                .seatingCapacity(lsRequest.getSeatingCapacity())
                .acceptReservations(lsRequest.getAcceptReservation())
                .isActive(true)
                .build();
        LocationSetting setting = this._settingService.save(locationSetting);
        _log.debug("create, Saved location setting id = {}", setting.getId());

        _log.info("create, Created new Location Setting from Business id = {}", request.getBusinessId());
        return locationSaved;
    }

    @Override
    public Location save(Location entity) {
        return this._repo.save(entity);
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
