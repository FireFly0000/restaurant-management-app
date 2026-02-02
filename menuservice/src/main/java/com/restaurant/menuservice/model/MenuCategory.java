package com.restaurant.menuservice.model;

import com.restaurant.commons.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Table(name = "menu_categories")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuCategory extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private UUID parentId;
    @Column(nullable = false)
    private UUID locationId;
    @Column(nullable = false)
    private String name;
    private String description;
    private String iconUrl;
    @Column(nullable = false)
    private String slug;
    private Boolean isActive;
}
