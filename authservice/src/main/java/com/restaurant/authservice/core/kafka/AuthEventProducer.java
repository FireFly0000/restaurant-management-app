package com.restaurant.authservice.core.kafka;

import com.restaurant.commons.core.rpc.notification.SendEmailEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AuthEventProducer {
    private static final Logger _log = LoggerFactory.getLogger(AuthEventProducer.class);

    private final KafkaProducerWrapper _kafka;

    public AuthEventProducer(
            KafkaProducerWrapper kafka
    ){
        this._kafka = kafka;
    }

    public void sendUserCreatedEvent(SendEmailEvent event){
        // Topic, key, message.
        // -> Topic:
        // -> key: Kafka ->  top -> partition -> 3
                // P1: 1,2,3,4,5,6,
                // P2: 8.9.10.11.12
                // P3: 13,14,15,16,17
        // -> message: Payload
//        SendEmailEvent event;
        _kafka.sendMessage("notify.email", String.valueOf(event.getUserId()), event);
    }
}
