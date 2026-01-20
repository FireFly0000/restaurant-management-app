package com.restaurant.userservice.core.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final KafkaProducerWrapper _kafka;

    private final NewTopic _userCreatedTopic;

    public UserEventProducer(
            KafkaProducerWrapper kafka,
            @Qualifier("userCreatedTopic") NewTopic userCreatedTopic
    ){
        this._kafka = kafka;
        this._userCreatedTopic = userCreatedTopic;
    }

    public void sendUserCreatedEvent(){

        _kafka.sendMessage(_userCreatedTopic.name(), "", new byte[100]);
    }
}
