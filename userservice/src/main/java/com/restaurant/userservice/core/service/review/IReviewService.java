package com.restaurant.userservice.core.service.review;

import com.restaurant.userservice.core.service.review.dto.CreateReviewRequest;
import com.restaurant.userservice.core.service.review.dto.UpdateReviewRequest;
import com.restaurant.userservice.model.Review;

import java.util.UUID;

public interface IReviewService {
    Review findById(UUID id);
    Review getByIdAndThrow(UUID id);
    Review createReview(CreateReviewRequest request);
    Review updateReview(UUID id, UpdateReviewRequest request);
    Review save(Review entity);

    boolean deleteReview(UUID id);
    boolean existsById(UUID id);
}
