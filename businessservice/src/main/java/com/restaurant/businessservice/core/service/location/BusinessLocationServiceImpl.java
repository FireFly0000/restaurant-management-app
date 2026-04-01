package com.restaurant.businessservice.core.service.location;

import com.restaurant.businessservice.common.MsgUtil;
import com.restaurant.businessservice.core.repository.IBusinessLocationRepository;
import com.restaurant.businessservice.core.rpc.IBusinessServiceRpcClient;
import com.restaurant.businessservice.core.service.location.dto.CreateLocationRequest;
import com.restaurant.businessservice.core.service.location.dto.CreateLocationSettingRequest;
import com.restaurant.businessservice.core.service.location.dto.UpdateLocationManagerRequest;
import com.restaurant.businessservice.core.service.location.dto.UpdateLocationRequest;
import com.restaurant.businessservice.core.service.outbox.BusinessOutboxService;
import com.restaurant.businessservice.model.Location;
import com.restaurant.businessservice.model.LocationSetting;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.constant.KafkaTopic;
import com.restaurant.commons.core.rpc.business.UpdatedLocationManagerEvent;
import com.restaurant.commons.core.rpc.user.GetUsersByIdsResponse;
import com.restaurant.commons.core.rpc.user.UserRpcResponse;
import com.restaurant.commons.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessLocationServiceImpl implements IBusinessLocationService{

    private static final Logger _log = LoggerFactory.getLogger(BusinessLocationServiceImpl.class);

    private final IBusinessLocationRepository _repo;
    private final MsgUtil _msgUtil;
    private final IBusinessLocationSettingService _settingService;
    private final IBusinessServiceRpcClient _businessServiceRpcClient;
    private final BusinessOutboxService _businessOutboxService;

    public BusinessLocationServiceImpl(
            IBusinessLocationRepository _repo,
            MsgUtil _msgUtil,
            IBusinessLocationSettingService _settingService,
            IBusinessServiceRpcClient _businessServiceRpcClient,
            BusinessOutboxService _businessOutboxService
    ){
        this._repo = _repo;
        this._msgUtil = _msgUtil;
        this._settingService = _settingService;
        this._businessServiceRpcClient = _businessServiceRpcClient;
        this._businessOutboxService = _businessOutboxService;
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
    public Location update(UUID id ,UpdateLocationRequest request) {
        _log.info("update, Start update location id = {}", id);
        Location location = this.getByIdAndThrow(id);
        location.setAddress(request.getAddress());
        location.setBranchName(request.getBranchName());
        location.setPhoneNumber(request.getPhoneNumber());
        location.setEmail(request.getEmail());
        location.setStartDate(request.getStartDate());

        this.save(location);
        _log.info("update, Updated location id = {}", id);

        return location;
    }

    @Override
    public Boolean active(UUID id) {
        _log.info("active, Start active location id = {}", id);
        Location location = this.getByIdAndThrow(id);
        location.setIsActive(true);
        this.save(location);
        _log.info("active, Updated location id = {}", id);

        return true;
    }

    @Override
    public Boolean inactive(UUID id) {
        _log.info("active, Start inactive location id = {}", id);
        Location location = this.getByIdAndThrow(id);
        location.setIsActive(false);
        this.save(location);
        _log.info("active, Updated location id = {}", id);

        return true;
    }

    @Override
    @Transactional
    public Boolean changeManager(UUID id, UpdateLocationManagerRequest request) {
        _log.info("changeManager, Start change Manager for location id = {}", id);
        Location location = this.getByIdAndThrow(id);

        UUID oldManagerId = location.getManagerId();
        UUID newManagerId = UUID.fromString(request.getManagerId());
        if(oldManagerId != null && oldManagerId.equals(newManagerId)){
            return true;
        }

        List<UUID> userIds = new ArrayList<>();
        userIds.add(newManagerId);
        if(oldManagerId != null){
            userIds.add(oldManagerId);
        }

        GetUsersByIdsResponse usersResponse = this._businessServiceRpcClient.getUsersByIds(userIds);
        if(usersResponse == null){
            throw new AppException(_msgUtil.getMessage("location.change_manager.failed"), Constant.RES3009, "400");
        }
        Map<String, UserRpcResponse> mapUser = usersResponse.getUsersList().stream()
                .collect(Collectors.toMap(UserRpcResponse::getId, v -> v));

        if(mapUser.get(request.getManagerId()) == null){
            throw new AppException(_msgUtil.getMessage("location.change_manager.not_exist"), Constant.RES3001, "400");
        }

        location.setManagerId(newManagerId);
        this.save(location);
        boolean oldManagerHasOtherLocations = oldManagerId != null
                && this._repo.existsOtherLocationByManagerId(oldManagerId, location.getId());
        UpdatedLocationManagerEvent.Builder eventBuilder = UpdatedLocationManagerEvent.newBuilder()
                .setLocationId(location.getId().toString())
                .setNewManagerId(newManagerId.toString())
                .setOldManagerHasOtherLocations(oldManagerHasOtherLocations);
        if (oldManagerId != null) {
            eventBuilder.setOldManagerId(oldManagerId.toString());
        }
        this._businessOutboxService.enqueue(
                KafkaTopic.LOCATION_MANAGER_UPDATED,
                location.getId().toString(),
                eventBuilder.build()
        );
        _log.info("changeManager, Changed manager for location id = {}", id);
        return true;
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
