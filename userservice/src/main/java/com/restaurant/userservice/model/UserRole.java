package com.restaurant.userservice.model;

import com.restaurant.userservice.model.embedded.UserRoleId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users_roles")
@Data
public class UserRole {
    @EmbeddedId
    private UserRoleId id;
}
