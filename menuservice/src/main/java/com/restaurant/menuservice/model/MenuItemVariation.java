package com.restaurant.menuservice.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.PriceAdjustment;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Table(name = "menu_item_variations")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuItemVariation extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private UUID menuItemId;
    @Column(nullable = false)
    private UUID variationTypeId;
    @Column(nullable = false)
    private String optionCode;
    @Column(nullable = false)
    private String optionName;
    private Double priceAdjustment;
    @Enumerated(EnumType.STRING)
    private PriceAdjustment privateAdjustmentType;
    private String imgUrl;
    private Boolean isAvailable;
}
