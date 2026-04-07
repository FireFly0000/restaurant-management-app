package com.restaurant.businessservice.core.repository;

import com.restaurant.businessservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBusinessLocationRepository extends JpaRepository<Location, UUID> {
    @Query("SELECT l FROM Location l WHERE l.businessId = :businessId AND l.isActive = true AND l.deletedAt = 0")
    List<Location> getBusinessLocationByBusinessId(UUID businessId);

    @Query("SELECT l FROM Location l WHERE l.businessId = :businessId AND l.isActive = :isActive AND l.deletedAt = 0")
    List<Location> getBusinessLocationByBusinessIdAndIsActive(UUID businessId, Boolean isActive);

    @Query("SELECT l FROM Location l WHERE l.businessId = :businessId AND l.deletedAt = 0")
    List<Location> getAllBusinessLocationByBusinessId(UUID businessId);

    @Query("SELECT l FROM Location l WHERE l.id = :id AND l.deletedAt = 0")
    Optional<Location> getLocationById(UUID id);

    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM Location l
        WHERE l.managerId = :managerId
          AND l.id <> :excludedLocationId
          AND l.deletedAt = 0
    """)
    boolean existsOtherLocationByManagerId(UUID managerId, UUID excludedLocationId);
}
