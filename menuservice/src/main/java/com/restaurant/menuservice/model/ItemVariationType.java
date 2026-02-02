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

@Table(name = "item_variation_types")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemVariationType extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private UUID businessId;
    @Column(nullable = false)
    private String typeCode;
    @Column(nullable = false)
    private String typeName;
    private String iconUrl;
    private Boolean isActive;
}
