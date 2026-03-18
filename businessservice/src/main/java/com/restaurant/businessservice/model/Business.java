package com.restaurant.businessservice.model;

import com.restaurant.commons.core.enums.BusinessType;
import com.restaurant.commons.core.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;


@Table(name = "businesses")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Business extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String name;
    private String description;
    private String avatarUrl;
    private String coverImgUrl;
    private String websiteUrl;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private BusinessType businessType;
    private Integer locationsCount;
    private Integer ordersCount;
    private Double averageRating = 5.0;

    @Version
    private Integer version;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = false;
        if(this.deletedAt == null){
            this.deletedAt = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
