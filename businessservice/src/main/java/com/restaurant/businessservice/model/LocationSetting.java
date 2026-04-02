package com.restaurant.businessservice.model;

import com.restaurant.commons.core.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Table(name = "location_settings")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
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

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
