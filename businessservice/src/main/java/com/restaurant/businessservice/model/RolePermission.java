package com.restaurant.businessservice.model;

import com.restaurant.businessservice.model.embedded.RolePermissionId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "roles_permissions")
@Entity
@Data
public class RolePermission {
    @EmbeddedId
    private RolePermissionId id;
}
