package com.restaurant.notification.core.service.sender;

import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.notification.core.service.sender.dto.EmailPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderImpl extends AbstractNotificationSender implements IEmailSender{

    @Override
    public NotiChannel getChannel(){
        return NotiChannel.EMAIL;
    }

    @Override
    public SendResult sendHtml(EmailPayload payload) {
        return null;
    }

    @Override
    public SendResult sendWithAttachment(EmailPayload payload) {
        return null;
    }
}
