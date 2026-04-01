package com.restaurant.businessservice.core.service.outbox;

import com.google.protobuf.Message;
import com.restaurant.businessservice.core.repository.IBusinessOutboxRepository;
import com.restaurant.businessservice.model.BusinessOutBox;
import com.restaurant.commons.core.enums.OutboxStatus;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BusinessOutboxService {
    private static final Logger _log = LoggerFactory.getLogger(BusinessOutboxService.class);
    private final IBusinessOutboxRepository _repo;
    private final KafkaProtobufSerializer<Message> _payloadSerializer;

    public BusinessOutboxService(
            IBusinessOutboxRepository repo,
            @Qualifier("outboxPayloadSerializer") KafkaProtobufSerializer<Message> payloadSerializer
    ) {
        this._repo = repo;
        this._payloadSerializer = payloadSerializer;
    }

    public void enqueue(String topic, String key, Message event) {
        _log.info("enqueue, Create outbox message for topic = {}, key = {}", topic, key);

        BusinessOutBox message = BusinessOutBox.builder()
                .topic(topic)
                .key(key)
                .payload(this._payloadSerializer.serialize(topic, event))
                .status(OutboxStatus.PENDING)
                .retry(0)
                .nextRetryAt(Instant.now())
                .build();

        this._repo.save(message);
        _log.info("enqueue, Created outbox message for topic = {}, key = {}", topic, key);
    }
}
