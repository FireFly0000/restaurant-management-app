package com.restaurant.commons.core;

import lombok.Data;
import jakarta.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@MappedSuperclass
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseEntity {
    protected Date createdAt;
    protected Date updatedAt;
    protected Long deletedAt;
}
