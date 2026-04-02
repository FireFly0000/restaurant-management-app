package com.restaurant.authservice.core.kafka;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.constant.KafkaTopic;
import com.restaurant.commons.core.rpc.notification.ResendExternalNotificationEvent;
import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class KafkaProducer {
    private static final Logger _log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaProducerWrapper _producer;
    public KafkaProducer(KafkaProducerWrapper producer) {
        _producer = producer;
    }

    public void pushUserCreatedEvent(String key, SendEmailEvent event, Map<String, String> headers){
        // Add EventID if it is not present.
        headers.putIfAbsent(Constant.H_EVENT_ID, UUID.randomUUID().toString());
        _log.info("pushUserCreatedEvent, EventId: {}", headers.get(Constant.H_EVENT_ID));

        _producer.sendMessage(KafkaTopic.USER_CREATED, key, event, headers);
        _log.info("pushUserCreatedEvent, Pushed event");
    }

    public void pushResendExternalNotificationEvent(
            String key,
            ResendExternalNotificationEvent event,
            Map<String, String> headers
    ){
        headers.putIfAbsent(Constant.H_EVENT_ID, UUID.randomUUID().toString());
        _log.info("pushResendExternalNotificationEvent, EventId: {}", headers.get(Constant.H_EVENT_ID));

        _producer.sendMessage(KafkaTopic.RESEND_EXTERNAL_NOTIFICATION, key, event, headers);
        _log.info("pushResendExternalNotificationEvent, Pushed event");
    }
}
