package com.restaurant.notification.model.embedded;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserNotificationId implements Serializable {
    private UUID userId;
    private UUID notificationId;

    public UserNotificationId(){}
    public UserNotificationId(UUID userId, UUID notificationId){
        this.userId = userId;
        this.notificationId = notificationId;
    }

    @Override
    public boolean equals(Object o){
        if(this == o ) return true;
        if(!(o instanceof  UserNotificationId that)) return false;
        return Objects.equals(userId, that.userId)
               && Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(userId, notificationId);
    }
}
