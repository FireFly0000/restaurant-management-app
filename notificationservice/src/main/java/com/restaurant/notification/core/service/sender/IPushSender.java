package com.restaurant.notification.core.service.sender;

import com.restaurant.notification.core.service.sender.dto.PushPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;

import java.util.List;
import java.util.UUID;

public interface IPushSender extends INotificationSender {
    SendResult sendToDevice(String fcmToken, PushPayload payload);
    List<SendResult> sendToUser(UUID userId, PushPayload payload);
    List<SendResult> sendToUsers(List<UUID> userIds, PushPayload payload);
}
