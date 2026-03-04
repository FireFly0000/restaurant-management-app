package com.restaurant.commons.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.MappedSuperclass;

import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {
    protected Date createdAt;
    protected Date updatedAt;
    protected Boolean isDeleted;
}
