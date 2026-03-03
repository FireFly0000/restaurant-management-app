package com.restaurant.notification.core.repository;

import com.restaurant.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, UUID> {
    @Query("SELECT n.eventId FROM Notification n where n.eventId IN :eventIds")
    Set<String> checkExistNotification(Collection<String> eventIds);
}
