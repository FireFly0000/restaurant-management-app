package com.restaurant.userservice.model;

import com.restaurant.userservice.model.embedded.ReviewId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @EmbeddedId
    private ReviewId id;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Float rating;
}
