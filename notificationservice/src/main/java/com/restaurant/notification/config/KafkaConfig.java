package com.restaurant.notification.config;

import com.restaurant.commons.constant.Constant;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    private final NotificationProperties _properties;

    public KafkaConfig(
            NotificationProperties properties
    ){
        this._properties = properties;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new TopicPartition(record.topic() + Constant.DLT, record.partition())
        );

        FixedBackOff backOff = new FixedBackOff(this._properties.getKafka().getRetry().getIntervalMs(), this._properties.getKafka().getRetry().getMaxAttempts());

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
