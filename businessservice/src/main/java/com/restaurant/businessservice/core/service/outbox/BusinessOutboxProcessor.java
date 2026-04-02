package com.restaurant.businessservice.core.service.outbox;

import com.restaurant.businessservice.config.BusinessProperties;
import com.restaurant.businessservice.core.repository.IBusinessOutboxRepository;
import com.restaurant.businessservice.model.BusinessOutBox;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.enums.OutboxStatus;
import com.restaurant.commons.exception.AppException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class BusinessOutboxProcessor {
    private static final Logger _log = LoggerFactory.getLogger(BusinessOutboxProcessor.class);

    private final IBusinessOutboxRepository _repo;
    private final KafkaTemplate<String, byte[]> _outboxKafkaTemplate;
    private final BusinessProperties _businessProperties;

    public BusinessOutboxProcessor(
            IBusinessOutboxRepository repo,
            @Qualifier("outboxKafkaTemplate") KafkaTemplate<String, byte[]> outboxKafkaTemplate,
            BusinessProperties businessProperties
    ) {
        this._repo = repo;
        this._outboxKafkaTemplate = outboxKafkaTemplate;
        this._businessProperties = businessProperties;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processMessage(UUID messageId) {
        _log.info("processMessage, Start process outbox message id = {}", messageId);
        BusinessOutBox entity = this._repo.findById(messageId)
                .orElseThrow(() -> {
                    _log.error("processMessage, Outbox message not found with id = {}", messageId);
                    return new AppException("Outbox message not found", Constant.RES3001, "400");
                });

        if (entity.getPayload() == null || entity.getPayload().length == 0) {
            _log.error("processMessage, Outbox payload is empty for message id = {}", entity.getId());
            throw new AppException("Outbox payload is empty", Constant.RES3004, "400");
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(Constant.H_EVENT_ID, resolveEventId(entity));

        sendSync(entity.getTopic(), entity.getKey(), entity.getPayload(), headers);

        entity.setStatus(OutboxStatus.SENT);
        entity.setMessage(null);
        this._repo.save(entity);

        _log.info("processMessage, Sent outbox message id = {}", entity.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailure(UUID messageId, Throwable ex) {
        _log.warn("handleFailure, Start update failed state for message id = {}", messageId);
        BusinessOutBox entity = this._repo.findById(messageId).orElse(null);
        if (entity == null || entity.getStatus() == OutboxStatus.SENT) {
            _log.warn("handleFailure, Skip update for message id = {}", messageId);
            return;
        }

        int currentRetry = entity.getRetry() == null ? 0 : entity.getRetry();
        int maxRetries = this._businessProperties.getOutbox().getMaxRetries();

        if (currentRetry >= maxRetries) {
            entity.setStatus(OutboxStatus.FAILED);
            entity.setMessage(ex.getMessage());
            _log.error("handleFailure, Marked outbox message id = {} as FAILED: {}", entity.getId(), ex.getMessage());
        } else {
            int nextRetry = currentRetry + 1;
            int delaySeconds = (int) Math.pow(2, nextRetry);

            entity.setStatus(OutboxStatus.RETRY);
            entity.setRetry(nextRetry);
            entity.setNextRetryAt(Instant.now().plusSeconds(delaySeconds));
            entity.setMessage(ex.getMessage());

            _log.warn("handleFailure, Scheduled retry for message id = {} retry = {} delay = {}s",
                    entity.getId(), nextRetry, delaySeconds);
        }

        this._repo.save(entity);
        _log.info("handleFailure, Updated failed state for message id = {}", messageId);
    }

    private void sendSync(String topic, String key, byte[] payload, Map<String, String> headers) {
        _log.debug("sendSync, Sending outbox payload to topic = {}, key = {}", topic, key);
        try {
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, payload);
            if (headers != null) {
                headers.forEach((k, v) -> {
                    if (v != null) {
                        record.headers().add(k, v.getBytes(StandardCharsets.UTF_8));
                    }
                });
            }
            SendResult<String, byte[]> result = this._outboxKafkaTemplate.send(record).get();
            _log.debug("sendSync, Sent outbox payload to topic = {}, partition = {}, offset = {}",
                    topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            _log.error("sendSync, Kafka send interrupted for topic = {}", topic, ex);
            throw new AppException("Kafka send interrupted", Constant.RES0004, "500", ex);
        } catch (ExecutionException ex) {
            _log.error("sendSync, Kafka send failed for topic = {}", topic, ex);
            throw new AppException("Kafka send failed", Constant.RES0003, "500", ex.getCause());
        }
    }

    private String resolveEventId(BusinessOutBox entity) {
        if (entity.getEventId() == null || entity.getEventId().isBlank()) {
            entity.setEventId(UUID.randomUUID().toString());
        }
        return entity.getEventId();
    }
}
