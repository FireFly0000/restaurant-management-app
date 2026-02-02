package com.restaurant.businessservice.model;

import com.restaurant.commons.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Table(name = "permissions")
@Entity
@Data
public class Permission extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    private String name;
    private String description;
    private Boolean isActive;
    @Column(nullable = false, unique = true)
    private String permissionCode;
}
