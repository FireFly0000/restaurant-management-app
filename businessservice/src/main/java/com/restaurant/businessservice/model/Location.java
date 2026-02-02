package com.restaurant.businessservice.model;

import com.restaurant.commons.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "locations")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Location extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    @Column(nullable = false)
    private UUID businessId;
    private UUID managerId;
    @Column(nullable = false)
    private String branchName;
    private String phoneNumber;
    private String email;
    private LocalDateTime startDate;
    private String address;
    private Boolean isActive;
}
