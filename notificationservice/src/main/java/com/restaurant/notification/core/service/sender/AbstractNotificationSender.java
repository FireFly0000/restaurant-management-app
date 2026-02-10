package com.restaurant.notification.core.service.sender;

import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.notification.core.service.sender.dto.AbstractNotificationPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;

import java.time.LocalDateTime;
import java.util.List;

public class AbstractNotificationSender implements INotificationSender {
    @Override
    public NotiChannel getChannel() {
        return null;
    }

    @Override
    public boolean validate(AbstractNotificationPayload payload) {
        return false;
    }

    @Override
    public SendResult send(AbstractNotificationPayload payload) {
        return null;
    }

    @Override
    public SendResult sendScheduled(AbstractNotificationPayload payload, LocalDateTime sendAt) {
        return null;
    }

    @Override
    public List<SendResult> sendBulk(List<AbstractNotificationPayload> payloads) {
        return List.of();
    }

    @Override
    public List<SendResult> sendBulkScheduled(List<AbstractNotificationPayload> payloads, LocalDateTime sendAt) {
        return List.of();
    }
}
