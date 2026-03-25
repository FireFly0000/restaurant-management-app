package com.restaurant.notification.core.kafka;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.constant.EmailTemplate;
import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.commons.core.enums.NotiStatus;
import com.restaurant.commons.core.enums.NotiType;
import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import com.restaurant.notification.core.service.notification.INotificationService;
import com.restaurant.notification.core.service.sender.IEmailSender;
import com.restaurant.notification.core.service.sender.dto.EmailPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;
import com.restaurant.notification.core.service.template.IEmailTemplateRenderer;
import com.restaurant.notification.model.Notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserEventHandler {
    private static final Logger _log = LoggerFactory.getLogger(UserEventHandler.class);

    private final IEmailSender _emailSender;
    private final INotificationService _notificationService;
    private final IEmailTemplateRenderer _templateRenderer;

    public UserEventHandler(
            IEmailSender _emailSender,
            INotificationService _notificationService,
            IEmailTemplateRenderer _templateRenderer
    ){
        this._emailSender = _emailSender;
        this._notificationService = _notificationService;
        this._templateRenderer = _templateRenderer;
    }

    @KafkaListener(topics = "user.created.event", containerFactory = "kafkaListenerContainerFactory", concurrency = "${app.notification.kafka.consumer.concurency}")
    public void handleUserCreatedEvent(List<ConsumerRecord<String, SendEmailEvent>> records, Acknowledgment ack){
        _log.info("handleUserCreatedEvent, Prepare to send {} email", records.size());
        try {
            List<String> eventIds =  new ArrayList<>();
            List<SendEmailEvent> events = new ArrayList<>();
            for (ConsumerRecord<String, SendEmailEvent> record : records) {
                Header header = record.headers().lastHeader(Constant.H_EVENT_ID);

                String eventId = header != null
                        ? new String(header.value())
                        : null;

                eventIds.add(eventId);
                events.add(record.value());
            }

            List<EmailPayload> payloads = new ArrayList<>();
            List<Notification> notifications = new ArrayList<>();
            Set<String> eventSuccessIds = this._notificationService.checkExistNotificationByEventIds(eventIds);
            for(int i = 0; i < events.size(); i++){
                SendEmailEvent event =  events.get(i);
                String eventId = eventIds.get(i);
                if(!eventSuccessIds.contains(eventId)){
                    _log.info("MY TEMPLATE: {}", event.getTemplateName());
                    Map<String, Object> templateVars = extractMetadata(event);
                    String renderedHtml = _templateRenderer.render(event.getTemplateName(), templateVars);

                    EmailPayload payload = EmailPayload.builder()
                            .to(new ArrayList<>(event.getToList()))
                            .subject(event.getSubject())
                            .bodyHtml(renderedHtml)
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
            _log.error("handleUserCreatedEvent, {}", ex.getMessage());

            throw new RuntimeException("handleUserCreatedEvent");
        }
    }

    // Convert google.protobuf.Struct metadata → Map<String, Object>
    private Map<String, Object> extractMetadata(SendEmailEvent event) {
        Map<String, Object> vars = new HashMap<>();

        if (event.hasMetadata()) {
            event.getMetadata().getFieldsMap().forEach((key, value) -> {
                switch (value.getKindCase()) {
                    case STRING_VALUE -> vars.put(key, value.getStringValue());
                    case NUMBER_VALUE -> vars.put(key, value.getNumberValue());
                    case BOOL_VALUE   -> vars.put(key, value.getBoolValue());
                    default           -> vars.put(key, value.toString());
                }
            });
        }

        return vars;
    }
}
