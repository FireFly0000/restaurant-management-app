package com.restaurant.notification.core.service.sender;

import com.restaurant.notification.core.service.sender.dto.EmailPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;

public interface IEmailSender extends INotificationSender{
    SendResult sendHtml(EmailPayload payload);
    SendResult sendWithAttachment(EmailPayload payload);

}
