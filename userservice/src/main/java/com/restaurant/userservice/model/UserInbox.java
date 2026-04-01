package com.restaurant.userservice.model;

import com.restaurant.commons.core.enums.InboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_inbox",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_inbox_consumer_name_event_id",
                        columnNames = {"consumer_name", "event_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_inbox_status_next_retry_at", columnList = "status, next_retry_at")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInbox {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private String consumerName;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String messageKey;

    @Column(nullable = false)
    private String eventId;

    @Lob
    @Column(nullable = false)
    private byte[] payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InboxStatus status;

    @Column(nullable = false)
    private Integer retry;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant nextRetryAt;

    private Instant processedAt;

    private String message;

    @PrePersist
    public void prePersist() {
        // Initialize retry metadata once so redelivery and re-claim can be handled safely.
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
            this.status = InboxStatus.PENDING;
        }
    }
}
