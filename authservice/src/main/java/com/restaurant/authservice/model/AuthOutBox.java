package com.restaurant.authservice.model;

import com.restaurant.commons.core.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_outbox", indexes = {
        @Index(name = "idx_auth_outbox_status_next_retry_at", columnList = "status, next_retry_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthOutBox {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    private String topic;
    private String key;
    private String eventId;
    @Lob
    @Column(nullable = false) private byte[] payload;
    @Enumerated(EnumType.STRING) private OutboxStatus status;
    private Integer retry;
    private Instant createdAt;
    private Instant nextRetryAt;
    private String message;

    @PrePersist public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.nextRetryAt == null) {
            this.nextRetryAt = Instant.now();
        }
        if (this.retry == null) {
            this.retry = 0;
        }
        if (this.status == null) {
            this.status = OutboxStatus.PENDING;
        }
        if (this.eventId == null || this.eventId.isBlank()) {
            this.eventId = UUID.randomUUID().toString();
        }
    }
}

