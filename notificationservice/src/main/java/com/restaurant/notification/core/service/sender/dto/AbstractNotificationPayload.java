package com.restaurant.notification.core.service.sender.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public abstract class AbstractNotificationPayload {
    /**
     * Recipient identifier (email, phone, user_id, device_token)
     */
    protected String recipient;

    /**
     * Notification type
     */
    protected String type;

    /**
     * Metadata
     */
    protected Map<String, Object> metadata;

    /**
     * User ID (optional)
     */
    protected Long userId;

    /**
     * Tracking ID (để link với notification_recipients)
     */
    protected Long recipientId;

    /**
     * Validate payload
     */
    public abstract boolean isValid();
}
