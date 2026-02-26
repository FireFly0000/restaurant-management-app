package com.restaurant.notification.core.service.sender.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SmsPayload extends AbstractNotificationPayload{
    private String phone;
    private String message;
    private String senderId;

    @Override
    public boolean isValid() {
        return phone != null && !phone.isEmpty()
               && message != null && !message.isEmpty()
               && message.length() <= 160; // SMS limit
    }
}
