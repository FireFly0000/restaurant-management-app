package com.restaurant.notification.core.kafka;

import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.commons.core.enums.NotiStatus;
import com.restaurant.commons.core.enums.NotiType;
import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import com.restaurant.notification.core.service.notification.INotificationService;
import com.restaurant.notification.core.service.sender.IEmailSender;
import com.restaurant.notification.core.service.sender.dto.EmailPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;
import com.restaurant.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class NotificationConsumer {
    private static final Logger _log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final IEmailSender _emailSender;
    private final INotificationService _notificationService;

    public NotificationConsumer(
        IEmailSender _emailSender,
        INotificationService _notificationService
    ){
        this._emailSender = _emailSender;
        this._notificationService = _notificationService;
    }

    @KafkaListener(topics = "notify.email", containerFactory = "kafkaListenerContainerFactory", concurrency = "${app.notification.kafka.consumer.concurency}")
    public void handleEmailNotification(@Payload List<SendEmailEvent> events, @Header("X-Event-Id") List<String> eventIds, Acknowledgment ack){
        _log.info("handleEmailNotification, Prepare to send {} email", events.size());
        try {
            List<EmailPayload> payloads = new ArrayList<>();
            List<Notification> notifications = new ArrayList<>();
            Set<String> eventSuccessIds = this._notificationService.checkExistNotificationByEventIds(eventIds);
            for(int i = 0; i < events.size(); i++){
                SendEmailEvent event =  events.get(i);
                String eventId = eventIds.get(i);
                if(!eventSuccessIds.contains(eventId)){
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
                            .eventId(eventId)
                            .build();

                    if(event.hasUserId()){
                        payload.setUserId(event.getUserId());
                    }
                    payloads.add(payload);

                    Notification notification = Notification.builder()
                            .type(NotiType.valueOf(payload.getType()))
                            .channel(NotiChannel.EMAIL)
                            .subject(payload.getSubject())
                            .bodyHtml(payload.getBodyHtml())
                            .totalRecipients(payload.getTo().size())
                            .eventId(eventId)
                            .status(NotiStatus.PENDING)
                            .build();
                    notifications.add(notification);
                }
            }

            List<Notification> savedNotifications = this._notificationService.saveAll(notifications);

            List<SendResult> results = _emailSender.sendBulk(payloads);

            this._notificationService.updateNotificationAfterSent(results, savedNotifications);

            ack.acknowledge();
        } catch(Exception ex){
            _log.error("handleEmailNotification, {}", ex.getMessage());

            throw new RuntimeException("handleEmailNotification");
        }
    }
}
