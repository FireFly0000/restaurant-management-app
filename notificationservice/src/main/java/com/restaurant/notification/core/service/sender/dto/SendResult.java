package com.restaurant.notification.core.service.sender.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class SendResult {
    private boolean success;
    private String recipient;
    private String channel;
    private String messageId;
    private String errorMessage;
    private String errorCode;
    private LocalDateTime sentAt;
    private Map<String, Object> metadata;

    public static SendResult success(String recipient, String channel, String messageId) {
        return SendResult.builder()
                .success(true)
                .recipient(recipient)
                .channel(channel)
                .messageId(messageId)
                .sentAt(LocalDateTime.now())
                .build();
    }

    public static SendResult failure(String recipient, String channel, String errorMessage, String errorCode) {
        return SendResult.builder()
                .success(false)
                .recipient(recipient)
                .channel(channel)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
