package com.restaurant.notification.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.DevicePlatform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "devices")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Device extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(unique = true)
    private String deviceId;

    private String deviceName;
    private String fcmToken;
    private DevicePlatform platform;
    private String osVersion;
    private String appVersion;
    private String deviceModel;
    private String manufacture;
    private String language;
    private Boolean isActive;
    private LocalDateTime lastActiveAt;
}
