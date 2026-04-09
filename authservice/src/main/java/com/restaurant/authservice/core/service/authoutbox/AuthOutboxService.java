package com.restaurant.authservice.core.service.authoutbox;

import com.google.protobuf.Message;
import com.restaurant.authservice.core.repository.IAuthOutboxRepository;
import com.restaurant.authservice.model.AuthOutBox;
import com.restaurant.commons.core.enums.OutboxStatus;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthOutboxService {
    private static final Logger _log = LoggerFactory.getLogger(AuthOutboxService.class);

    private final IAuthOutboxRepository _repo;
    private final KafkaProtobufSerializer<Message> _payloadSerializer;

    public AuthOutboxService(
            IAuthOutboxRepository repo,
            @Qualifier("outboxPayloadSerializer") KafkaProtobufSerializer<Message> payloadSerializer
    ) {
        this._repo = repo;
        this._payloadSerializer = payloadSerializer;
    }

    @Transactional
    public void enqueue(String topic, String key, Message event) {
        _log.info("enqueue, Create outbox message for topic = {}, key = {}", topic, key);

        AuthOutBox record = AuthOutBox.builder()
                .topic(topic).key(key)
                .payload(_payloadSerializer.serialize(topic, event))
                .status(OutboxStatus.PENDING)
                .retry(0)
                .nextRetryAt(Instant.now())
                .build();

        this._repo.save(record);
        _log.info("enqueue, Created outbox message for topic = {}, key = {}", topic, key);
    }
}

