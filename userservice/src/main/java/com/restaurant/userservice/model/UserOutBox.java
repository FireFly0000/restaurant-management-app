package com.restaurant.userservice.model;

import com.restaurant.commons.enums.OutboxStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_outbox", indexes = {
        @Index(name = "idx_user_outbox_status", columnList = "status")
})
public class UserOutBox {
    @Id
    private UUID id;
    private String topic;
    private String key;
    @Lob
    private byte[] payload;
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    private Integer retry;
    private Instant createdAt;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
    }
}
