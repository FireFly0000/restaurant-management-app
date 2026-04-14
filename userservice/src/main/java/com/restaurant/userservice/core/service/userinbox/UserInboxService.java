package com.restaurant.userservice.core.service.userinbox;

import com.google.protobuf.Message;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.constant.KafkaTopic;
import com.restaurant.commons.core.enums.InboxStatus;
import com.restaurant.commons.exception.AppException;
import com.restaurant.userservice.config.UserProperties;
import com.restaurant.userservice.core.repository.IUserInboxRepository;
import com.restaurant.userservice.model.UserInbox;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserInboxService {
    private static final Logger _log = LoggerFactory.getLogger(UserInboxService.class);

    private final IUserInboxRepository _repo;
    private final UserProperties _userProperties;

    public UserInboxService(
            IUserInboxRepository repo,
            UserProperties userProperties
    ) {
        this._repo = repo;
        this._userProperties = userProperties;
    }

    @Transactional
    public <T extends Message> void enqueue(String consumerName, List<ConsumerRecord<String, T>> records) {
        if (CollectionUtils.isEmpty(records)) {
            _log.debug("enqueue, No records to enqueue for consumer = {}", consumerName);
            return;
        }

        List<String> eventIds = records.stream()
                .map(this::resolveEventId)
                .toList();
        Set<String> existingEventIds = new HashSet<>(this._repo.findByConsumerNameAndEventIdIn(consumerName, eventIds)
                .stream()
                .map(UserInbox::getEventId)
                .toList());

        List<UserInbox> inboxMessages = new ArrayList<>();
        Instant now = Instant.now();

        for (ConsumerRecord<String, T> record : records) {
            String eventId = resolveEventId(record);
            if (existingEventIds.contains(eventId)) {
                _log.debug("enqueue, Skip duplicated eventId = {} for consumer = {}", eventId, consumerName);
                continue;
            }

            if (record.value() == null) {
                throw new AppException("Inbox payload is empty.", Constant.RES3004, "400");
            }

            inboxMessages.add(UserInbox.builder()
                    .consumerName(consumerName)
                    .topic(record.topic())
                    .messageKey(record.key() == null ? "" : record.key())
                    .eventId(eventId)
                    .payload(record.value().toByteArray())
                    .status(InboxStatus.PENDING)
                    .retry(0)
                    .createdAt(now)
                    .nextRetryAt(now)
                    .build());
        }

        if (inboxMessages.isEmpty()) {
            _log.info("enqueue, No new inbox messages to persist for consumer = {}", consumerName);
            return;
        }

        this._repo.saveAll(inboxMessages);
        _log.info("enqueue, Persisted {} inbox messages for consumer = {}", inboxMessages.size(), consumerName);
    }

    @Transactional
    public List<UserInbox> claimNextBatch() {
        List<UserInbox> messages = this._repo.findBatchToProcess(this._userProperties.getInbox().getBatchSize());
        if (CollectionUtils.isEmpty(messages)) {
            return List.of();
        }

        Instant leaseExpiredAt = Instant.now().plusSeconds(this._userProperties.getInbox().getLeaseSeconds());
        for (UserInbox message : messages) {
            message.setStatus(InboxStatus.PROCESSING);
            message.setNextRetryAt(leaseExpiredAt);
            message.setMessage(null);
        }

        _log.info("claimNextBatch, Claimed {} inbox messages", messages.size());
        return messages;
    }

    private <T extends Message> String resolveEventId(ConsumerRecord<String, T> record) {
        Header eventIdHeader = record.headers().lastHeader(Constant.H_EVENT_ID);
        if (eventIdHeader != null && eventIdHeader.value() != null && eventIdHeader.value().length > 0) {
            return new String(eventIdHeader.value(), StandardCharsets.UTF_8);
        }

        // Fallback to a deterministic identifier so redelivery of the same record is still idempotent.
        String fallbackEventId = String.format(
                "%s-%s-%s-%s",
                record.topic(),
                record.partition(),
                record.offset(),
                KafkaTopic.LOCATION_MANAGER_UPDATED.equals(record.topic()) ? "typed" : "generic"
        );
        _log.warn("resolveEventId, Missing eventId header, fallback eventId = {}", fallbackEventId);
        return fallbackEventId;
    }
}
