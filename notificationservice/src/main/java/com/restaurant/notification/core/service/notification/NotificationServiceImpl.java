package com.restaurant.notification.core.service.notification;

import com.restaurant.commons.core.enums.NotiStatus;
import com.restaurant.notification.core.repository.INotificationRepository;
import com.restaurant.notification.core.service.sender.dto.SendResult;
import com.restaurant.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository _repo;
    private static final Logger _log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl(INotificationRepository repo) {
        this._repo = repo;
    }

    @Override
    public Notification save(Notification entity) {
        return _repo.save(entity);
    }

    @Override
    public List<Notification> saveAll(Collection<Notification> entities) {
        return _repo.saveAll(entities);
    }

    @Override
    public Set<String> checkExistNotificationByEventIds(Collection<String> eventIds) {
        if(eventIds == null || eventIds.isEmpty()){
            return Set.of();
        }
        return this._repo.checkExistNotification(eventIds);
    }

    @Override
    @Transactional
    public List<Notification> updateNotificationAfterSent(List<SendResult> argResults, List<Notification> argNotifications) {
        Map<String, SendResult> sendResultMap = argResults.stream()
                .collect(Collectors.toMap(
                        SendResult::getEventId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        for(Notification notification : argNotifications){
            SendResult result = sendResultMap.get(notification.getEventId());
            if(result != null){
                notification.setStatus(result.isSuccess() ? NotiStatus.SENT : NotiStatus.FAILED);
            }else{
                _log.warn("updateNotificationAfterSent, No SendResult found for eventId: {}", notification.getEventId());
            }
        }

        return _repo.saveAll(argNotifications);
    }
}
