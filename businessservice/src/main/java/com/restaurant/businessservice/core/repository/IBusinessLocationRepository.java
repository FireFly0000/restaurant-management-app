package com.restaurant.businessservice.core.repository;

import com.restaurant.businessservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IBusinessLocationRepository extends JpaRepository<Location, UUID> {

}
