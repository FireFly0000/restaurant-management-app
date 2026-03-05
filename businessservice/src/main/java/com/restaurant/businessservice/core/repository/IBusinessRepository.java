package com.restaurant.businessservice.core.repository;

import com.restaurant.businessservice.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBusinessRepository extends JpaRepository<Business, UUID> {
    @Query("SELECT b FROM Business b WHERE b.id = :id AND b.isActive = true AND b.isDeleted = false")
    Optional<Business> getByIdActiveTrueAndDeleteFalse(UUID id);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}