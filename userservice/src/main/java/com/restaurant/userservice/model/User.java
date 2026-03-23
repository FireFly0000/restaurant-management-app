package com.restaurant.userservice.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private LocalDateTime birthDate;
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Boolean isActive;
    private Boolean isVerified;
    private Boolean isDeleted;

    @PrePersist
    private void prePersist(){
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}
