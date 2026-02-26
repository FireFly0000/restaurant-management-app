package com.restaurant.notification.core.service.sender;

import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.notification.core.service.sender.dto.AbstractNotificationPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;

import java.time.LocalDateTime;
import java.util.List;

public interface INotificationSender<T extends AbstractNotificationPayload> {
    NotiChannel getChannel();
    boolean validate(T payload);

    SendResult send(T payload);
    SendResult sendScheduled(T payload, LocalDateTime sendAt);

    List<SendResult> sendBulk(List<T> payloads);
    List<SendResult> sendBulkScheduled(List<T> payloads, LocalDateTime sendAt);
}
