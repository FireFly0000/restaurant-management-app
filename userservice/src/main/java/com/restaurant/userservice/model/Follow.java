package com.restaurant.userservice.model;

import com.restaurant.userservice.model.embedded.FollowId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "follows")
@Data
@Entity
public class Follow {
    @EmbeddedId
    private FollowId id;
    private LocalDateTime followedAt;
}
