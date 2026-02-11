package com.restaurant.notification.core.service.sender.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class PushPayload extends AbstractNotificationPayload{
    private String title;
    private String body;
    private String imageUrl;
    private String icon;
    private String sound;
    private String clickAction;
    private Map<String, String> data; // Custom data
    private String badge; // Badge count for iOS

    @Override
    public boolean isValid() {
        return (title != null && body != null) || (data != null && !data.isEmpty());
    }
}
