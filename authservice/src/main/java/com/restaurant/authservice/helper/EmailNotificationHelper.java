package com.restaurant.authservice.helper;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.restaurant.authservice.core.service.authoutbox.AuthOutboxService;
import com.restaurant.authservice.utils.Utils;
import com.restaurant.commons.constant.ContactType;
import com.restaurant.commons.constant.NotificationPurpose;
import com.restaurant.commons.core.enums.NotiType;
import com.restaurant.commons.core.rpc.notification.ResendExternalNotificationEvent;
import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailNotificationHelper {
    private final Logger _log = LoggerFactory.getLogger(EmailNotificationHelper.class);
    private final AuthOutboxService _authOutboxService;

    public EmailNotificationHelper(
            AuthOutboxService authOutboxService
    ){
        this._authOutboxService = authOutboxService;
    }


    public void sendEmail(
            String userId,
            String email,
            String subject,
            String templateName,
            String kafkaTopic,
            Map<String, Value> metadataFields
    ) {
        Struct metadata = Struct.newBuilder()
                .putAllFields(metadataFields)
                .build();

        SendEmailEvent event = SendEmailEvent.newBuilder()
                .setRecipient(email)
                .setUserId(userId)
                .setRecipientId(userId)
                .addTo(email)
                .setSubject(subject)
                .setFrom("no-reply@restaurant.com")
                .setType(NotiType.SYSTEM.name())
                .setTemplateName(templateName)
                .setMetadata(metadata)
                .build();

        _authOutboxService.enqueue(kafkaTopic, userId, event);
        _log.info("sendEmail, enqueued topic={}, template={}, userId={}", kafkaTopic, templateName, userId);
    }

    //RESEND LOGIC FOR BOTH EMAIL AND SMS
    public void resendExternalNotification(
            String userId,
            String email,
            String templateName,
            String kafkaTopic,
            Map<String, Value> metadataFields,
            ContactType contactType
    ){
        Struct metadata = Struct.newBuilder()
                .putAllFields(metadataFields)
                .build();

        ResendExternalNotificationEvent event = ResendExternalNotificationEvent.newBuilder()
                .setRecipient(email != null ? email : "")
                .setRecipientId(userId)
                .setUserId(userId)
                .setContactType(contactType.name())
                .setPurpose(NotificationPurpose.RESEND_VERIFY_EMAIL.name())
                .addTo(email != null ? email : "")
                .setSubject(getEmailSubject(NotificationPurpose.RESEND_VERIFY_EMAIL))
                .setFrom("no-reply@restaurant.com")
                .setTemplateName(templateName)
                .setMetadata(metadata)
                .setType(NotiType.SYSTEM.name())
                .build();

        _authOutboxService.enqueue(kafkaTopic, userId, event);
        _log.info("resendExternalNotification, enqueued topic={}, template={}, userId={}",
                kafkaTopic, templateName, userId
        );
    }

    public String getEmailSubject(NotificationPurpose purpose) {
        return switch (purpose) {
            case VERIFY         -> "Please verify your account!";
            case RESEND_VERIFY_EMAIL  -> "New verification link";
            case RESET_PASSWORD -> "Reset your password";
            case RESEND_RESET_PASSWORD_EMAIL -> "New reset password link";
        };
    }
}
