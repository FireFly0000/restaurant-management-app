package com.restaurant.businessservice.model;

import com.restaurant.commons.core.enums.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "business_outbox", indexes = {
        @Index(name = "idx_business_outbox_status_next_retry_at", columnList = "status, next_retry_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessOutBox {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private String topic;
    private String key;
    private String eventId;

    @Lob
    @Column(nullable = false)
    private byte[] payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Integer retry;
    private Instant createdAt;
    private Instant nextRetryAt;
    private String message;

    @PrePersist
    public void prePersist() {
        // Initialize delivery metadata once so the worker can process and retry safely.
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
