package com.restaurant.notification.core.service.notification;

import com.restaurant.notification.core.service.sender.dto.SendResult;
import com.restaurant.notification.model.Notification;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface INotificationService {
    Notification save(Notification entity);
    List<Notification> saveAll(Collection<Notification> entities);
    Set<String> checkExistNotificationByEventIds(Collection<String> eventIds);
    List<Notification> updateNotificationAfterSent(List<SendResult> result, List<Notification> notifications);
}
