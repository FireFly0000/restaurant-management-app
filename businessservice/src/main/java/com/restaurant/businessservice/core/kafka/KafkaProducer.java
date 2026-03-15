package com.restaurant.businessservice.core.kafka;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.constant.KafkaTopic;
import com.restaurant.commons.core.rpc.storage.FileCleanUpEvent;
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

    public void pushStorageFileCleanUpEvent(String key, FileCleanUpEvent event, Map<String, String> headers){
        // Add EventID if it is not present.
        headers.putIfAbsent(Constant.H_EVENT_ID, UUID.randomUUID().toString());
        _log.info("pushStorageFileCleanUpEvent, EventId: {}", headers.get(Constant.H_EVENT_ID));

        _producer.sendMessage(KafkaTopic.FILE_CLEANUP, key, event, headers);
        _log.info("pushStorageFileCleanUpEvent, Pushed event");
    }
}
