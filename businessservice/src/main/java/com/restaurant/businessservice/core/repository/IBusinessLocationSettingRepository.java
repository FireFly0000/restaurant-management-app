package com.restaurant.businessservice.core.repository;

import com.restaurant.businessservice.model.LocationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IBusinessLocationSettingRepository extends JpaRepository<LocationSetting, UUID> {
    boolean existsByLocationId(UUID locationId);
}
