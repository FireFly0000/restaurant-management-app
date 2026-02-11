package com.restaurant.notification.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.commons.core.enums.NotiType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "notifications")
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private NotiType type;
    private NotiChannel channel;

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
}
