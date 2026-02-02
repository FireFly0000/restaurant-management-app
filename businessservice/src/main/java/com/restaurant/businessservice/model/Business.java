package com.restaurant.businessservice.model;

import com.restaurant.commons.core.enums.BusinessType;
import com.restaurant.commons.core.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Table(name = "businesses")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Business extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String name;
    private String description;
    private String avatarUrl;
    private String coverImgUrl;
    private String websiteUrl;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private BusinessType businessType;
}
