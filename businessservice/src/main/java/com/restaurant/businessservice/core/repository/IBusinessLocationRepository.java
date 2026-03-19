package com.restaurant.businessservice.core.repository;

import com.restaurant.businessservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IBusinessLocationRepository extends JpaRepository<Location, UUID> {
    @Query("SELECT l FROM Location l WHERE l.businessId = :businessId AND l.isActive = true AND l.deletedAt = 0")
    List<Location> getBusinessLocationByBusinessId(UUID businessId);
}
