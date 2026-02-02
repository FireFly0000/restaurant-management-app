package com.restaurant.commons.core;

import lombok.Getter;

@Getter
public class UserContext {
    private final String userId;

    public UserContext(String userId) {
        this.userId = userId;
    }
}
