package com.restaurant.businessservice.model;

import com.restaurant.commons.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Table(name = "location_settings")
@Entity
@Data
public class LocationSetting extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    @Column(nullable = false)
    private UUID locationId;

    private String settingName;
    private String openingHours;
    private Boolean hasParking;
    private Integer seatingCapacity;
    private Boolean acceptReservations;
    private Boolean isActive;
}
