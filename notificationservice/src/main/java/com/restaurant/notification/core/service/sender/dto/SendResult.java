package com.restaurant.notification.core.service.sender.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@SuperBuilder
public class SendResult {
    private boolean success;
    private String recipient;
    private String channel;
    private String eventId;
    private String errorMessage;
    private String errorCode;
    private LocalDateTime sentAt;
    private Map<String, Object> metadata;

    public static SendResult success(String recipient, String channel, String eventId) {
        return SendResult.builder()
                .success(true)
                .recipient(recipient)
                .channel(channel)
                .eventId(eventId)
                .sentAt(LocalDateTime.now())
                .build();
    }

    public static SendResult failure(String recipient, String channel, String eventId, String errorMessage, String errorCode) {
        return SendResult.builder()
                .success(false)
                .recipient(recipient)
                .channel(channel)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .sentAt(LocalDateTime.now())
                .eventId(eventId)
                .build();
    }
}
