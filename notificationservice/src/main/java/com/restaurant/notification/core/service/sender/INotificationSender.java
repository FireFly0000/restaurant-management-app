package com.restaurant.notification.core.service.sender;

import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.notification.core.service.sender.dto.AbstractNotificationPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;

import java.time.LocalDateTime;
import java.util.List;

public interface INotificationSender {
    NotiChannel getChannel();
    boolean validate(AbstractNotificationPayload payload);

    SendResult send(AbstractNotificationPayload payload);
    SendResult sendScheduled(AbstractNotificationPayload payload, LocalDateTime sendAt);

    List<SendResult> sendBulk(List<AbstractNotificationPayload> payloads);
    List<SendResult> sendBulkScheduled(List<AbstractNotificationPayload> payloads, LocalDateTime sendAt);
}
