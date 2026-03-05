package com.restaurant.notification.core.service.sender;

import com.restaurant.notification.core.service.sender.dto.EmailPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;
import jakarta.mail.MessagingException;

public interface IEmailSender extends INotificationSender<EmailPayload>{
    SendResult sendHtml(EmailPayload payload) throws MessagingException;
    SendResult sendWithAttachment(EmailPayload payload);

}
