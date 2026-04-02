package com.restaurant.commons.constant;

public class KafkaTopic {
    // Notification Service
    public static final String SEND_EMAIL = "notify.send_email";
    public static final String USER_CREATED = "user.created.event";
    public static final String RESEND_EXTERNAL_NOTIFICATION = "notification.external.resend";

    // Storage Service
    public static final String FILE_CLEANUP = "storage.file-cleanup";

    // Business Service
    public static final String LOCATION_MANAGER_UPDATED = "business.location-manager.updated";
    public static final String LOCATION_MANAGER_UPDATE_FAILED = "business.location-manager.update-failed";
}
