package com.restaurant.notification.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.DevicePlatform;
import com.restaurant.commons.core.enums.NotiStatus;
import com.restaurant.notification.model.embedded.UserNotificationId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Table(name = "notifications_recipients")
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class NotificationRecipients extends BaseEntity {
    @EmbeddedId
    private UserNotificationId id;

    private String recipient; // email, phone, device_token
    private DevicePlatform platform;
    private NotiStatus status;
    private String errorMessage;
    private Boolean isSeen;
    private LocalDateTime seenAt;
}
