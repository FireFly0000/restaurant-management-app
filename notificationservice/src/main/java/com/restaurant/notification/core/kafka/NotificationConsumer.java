package com.restaurant.notification.core.kafka;

import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import com.restaurant.notification.core.service.sender.IEmailSender;
import com.restaurant.notification.core.service.sender.dto.EmailPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class NotificationConsumer {
    private static final Logger _log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final IEmailSender _emailSender;

    public NotificationConsumer(
        IEmailSender _emailSender
    ){
        this._emailSender = _emailSender;
    }

    @KafkaListener(topics = "notify.email", containerFactory = "kafkaListenerContainerFactory", concurrency = "${app.notification.kafka.consumer.concurency}")
    public void handleEmailNotification(@Payload List<SendEmailEvent> events, Acknowledgment ack){
        _log.info("handleEmailNotification, Prepare to send {} email", events.size());
        try {
            List<EmailPayload> payloads = new ArrayList<>();
            for(SendEmailEvent event : events){
                EmailPayload payload = EmailPayload.builder()
                        .to(new ArrayList<>(event.getToList()))
                        .subject(event.getSubject())
                        .bodyHtml(event.getBodyHtml())
                        .bodyText(event.getBodyText())
                        .from(event.getFrom())
                        .replyTo(new ArrayList<>(event.getReplyToList()))
                        .cc(new ArrayList<>(event.getCcList()))
                        .bcc(new ArrayList<>(event.getBccList()))
                        .recipient(event.getRecipient())
                        .type(event.getType())
                        .recipientId(event.getRecipientId())
                        .build();

                if(event.hasUserId()){
                    payload.setUserId(event.getUserId());
                }
                payloads.add(payload);
            }

            _emailSender.sendBulk(payloads);

            ack.acknowledge();
        } catch(Exception ex){
            _log.error("handleEmailNotification, {}", ex.getMessage());

            throw new RuntimeException("handleEmailNotification");
        }
    }
}
