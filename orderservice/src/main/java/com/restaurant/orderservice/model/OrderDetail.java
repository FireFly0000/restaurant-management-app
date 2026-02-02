package com.restaurant.orderservice.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.ItemType;
import com.restaurant.commons.core.enums.LineType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Table(name = "order_details")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetail extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;
    @Column(nullable = false)
    private UUID itemId;
    @Column(nullable = false)
    private UUID combinationId;
    private UUID parentId;
    private UUID dealId;
    private String dealCode;
    private UUID lineId;
    private Integer lineSeq;
    private Double currentPrice;
    private Double previousPrice;
    @Column(nullable = false)
    private String itemName;
    @Column(nullable = false)
    private Integer quantity;
    private Double percentDiscount;
    private Double amountDiscount;
    @Column(nullable = false)
    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LineType lineType;
}
