package com.restaurant.commons.core.enums;

public enum OutboxStatus {
    PENDING,
    PROCESSING,
    SENT,
    RETRY,
    FAILED,
}
