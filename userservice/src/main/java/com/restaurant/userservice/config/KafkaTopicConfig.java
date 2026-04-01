package com.restaurant.userservice.config;

import com.restaurant.commons.constant.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean("userCreatedTopic")
    public NewTopic userCreatedTopic(){
        return TopicBuilder.name("user.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean("locationManagerUpdatedTopic")
    public NewTopic locationManagerUpdatedTopic() {
        return TopicBuilder.name(KafkaTopic.LOCATION_MANAGER_UPDATED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
