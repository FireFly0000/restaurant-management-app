package com.restaurant.orderservice.model;

import com.restaurant.commons.core.BaseEntity;
import com.restaurant.commons.core.enums.OrderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "orders")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private UUID customerId;
    @Column(nullable = false)
    private UUID businessId;
    @Column(nullable = false)
    private UUID locationId;
    @Column(nullable = false)
    private UUID employeeId;
    @Column(nullable = false)
    private OrderType orderType;
    private Integer numberOrPeople;
    private LocalDateTime arrivalTime;
    private Integer totalItem;
    private String note;
    private Double subTotal;
    private Double totalAmount;
    private Boolean isAccepted;
    private String dealsApplied;
}
