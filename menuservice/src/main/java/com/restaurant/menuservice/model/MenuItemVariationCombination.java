package com.restaurant.menuservice.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.CalculationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Table(name = "menu_item_variation_combinations")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuItemVariationCombination extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private UUID menuItemId;
    private String combinationName;
    private String combinationCode;
    private String variationIds; // String arr
    private String variationDetails;
    private Double priceOveride;
    @Enumerated(EnumType.STRING)
    private CalculationType priceCalculationType;
    private Double finalPrice;
    private Double costPrice;
    private Integer stockQuantity;
    @Column(nullable = false, unique = true)
    private String sku;
    private String igmUrl;
}
