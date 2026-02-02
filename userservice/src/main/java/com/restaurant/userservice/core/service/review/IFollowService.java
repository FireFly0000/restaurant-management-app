package com.restaurant.userservice.core.service.review;

import java.util.UUID;

public interface IFollowService {
    boolean follow(UUID bussinessId);
    boolean unfollow(UUID bussinessId);
}
