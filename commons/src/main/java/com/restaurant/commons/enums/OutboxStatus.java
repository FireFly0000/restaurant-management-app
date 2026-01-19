package com.restaurant.commons.enums;

public enum OutboxStatus {
    PENDING,
    SENT,
    RETRY,
    FAILED,
}
