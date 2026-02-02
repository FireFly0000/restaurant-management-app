package com.restaurant.menuservice.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.ItemType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.UUID;

@Table(name = "menu_items")
@EnableAsync
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuItem extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private UUID menuCategoryId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType itemType;
    private String name;
    private String description;
    private String slug;
    private Double basePrice;
    private Double currentPrice;
    private Double discountPrice;
    private Double discountPercent;
    private Double taxRate;
    private Double costPrice;
    private Boolean isActive;
}
