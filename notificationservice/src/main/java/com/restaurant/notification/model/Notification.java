package com.restaurant.notification.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.commons.core.enums.NotiStatus;
import com.restaurant.commons.core.enums.NotiType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Table(name = "notifications")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Notification extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private NotiType type;
    @Enumerated(EnumType.STRING)
    private NotiChannel channel;
    @Enumerated(EnumType.STRING)
    private NotiStatus status;

    private String title;
    private String message;

    private String subject; // for email
    @Column(columnDefinition = "TEXT")
    private String bodyHtml;

    private Integer totalRecipients;
    private Integer sentCount;
    private Integer failedCount;

    private LocalDateTime sentAt;
    private LocalDateTime startAt;
    private LocalDateTime completedAt;

    @Column(unique = true)
    private String eventId;

    @Version
    private Integer version;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}
