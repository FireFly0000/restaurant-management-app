package com.restaurant.authservice.core.kafka;

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
            KafkaProducerWrapper kafka,
            @Qualifier("userCreatedTopic") NewTopic userCreatedTopic
    ){
        this._kafka = kafka;
    }

    public void sendUserCreatedEvent(){
        SendEmailEvent
        _kafka.sendMessage("notify.email", );
    }
}
