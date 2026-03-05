package com.restaurant.notification.core.service.sender.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EmailPayload extends AbstractNotificationPayload {
    private List<String> to;
    private String subject;
    private String bodyText;
    private String bodyHtml;
    private String from;
    private List<String> replyTo;
    private List<String> cc;
    private List<String> bcc;

    @Override
    public boolean isValid() {
        return to != null && !to.isEmpty()
               && subject != null && !subject.isEmpty()
               && (bodyText != null || bodyHtml != null);
    }
}
