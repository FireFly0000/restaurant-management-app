package com.restaurant.userservice.core.service.userinbox;

import com.google.protobuf.Message;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.enums.InboxStatus;
import com.restaurant.commons.core.service.inbox.InboxEventHandler;
import com.restaurant.commons.exception.AppException;
import com.restaurant.userservice.config.UserProperties;
import com.restaurant.userservice.core.repository.IUserInboxRepository;
import com.restaurant.userservice.model.UserInbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserInboxProcessor {
    private static final Logger _log = LoggerFactory.getLogger(UserInboxProcessor.class);

    private final IUserInboxRepository _inboxRepo;
    private final UserProperties _userProperties;
    private final Map<String, InboxEventHandler<?>> _handlersByTopic;
    private final TransactionTemplate _txTemplate;

    public UserInboxProcessor(
            IUserInboxRepository inboxRepo,
            UserProperties userProperties,
            List<InboxEventHandler<?>> handlers,
            PlatformTransactionManager txManager
    ) {
        this._inboxRepo = inboxRepo;
        this._userProperties = userProperties;
        this._handlersByTopic = handlers.stream()
                .collect(Collectors.toMap(InboxEventHandler::topic, h -> h));
        this._txTemplate = new TransactionTemplate(txManager);
        this._txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        _log.info("UserInboxProcessor, Registered {} inbox handler(s): {}",
                _handlersByTopic.size(), _handlersByTopic.keySet());
    }

    public void processBatch(List<UserInbox> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            _log.debug("processBatch, No inbox messages to process");
            return;
        }

        Map<String, List<UserInbox>> messagesByTopic = messages.stream()
                .collect(Collectors.groupingBy(UserInbox::getTopic, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<UserInbox>> entry : messagesByTopic.entrySet()) {
            InboxEventHandler<?> handler = this._handlersByTopic.get(entry.getKey());
            if (handler == null) {
                _log.error("processBatch, No handler registered for topic = {}, marking {} message(s) as FAILED",
                        entry.getKey(), entry.getValue().size());
                markFailed(entry.getValue(), new AppException(
                        String.format("No inbox handler registered for topic: %s", entry.getKey()),
                        Constant.RES3003, "400"));
                continue;
            }

            if (handler.isBatchHandler()) {
                dispatchBatch(handler, entry.getValue());
            } else {
                dispatchPerMessage(handler, entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Message> void dispatchBatch(InboxEventHandler<?> rawHandler, List<UserInbox> messages) {
        InboxEventHandler<T> handler = (InboxEventHandler<T>) rawHandler;
        try {
            this._txTemplate.executeWithoutResult(status -> {
                List<T> events = new ArrayList<>(messages.size());
                for (UserInbox message : messages) {
                    events.add(handler.parse(message.getPayload()));
                }

                handler.handleBatch(events);
                markProcessed(messages);
            });
            _log.info("dispatchBatch, Processed {} messages for topic = {}", messages.size(), handler.topic());
        } catch (Exception ex) {
            _log.error("dispatchBatch, Batch failed for topic = {}", handler.topic(), ex);
            markFailed(messages, ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Message> void dispatchPerMessage(InboxEventHandler<?> rawHandler, List<UserInbox> messages) {
        InboxEventHandler<T> handler = (InboxEventHandler<T>) rawHandler;
        for (UserInbox message : messages) {
            try {
                this._txTemplate.executeWithoutResult(status -> {
                    T event = handler.parse(message.getPayload());
                    handler.handle(event);
                    markProcessed(message);
                });
            } catch (Exception ex) {
                _log.error("dispatchPerMessage, Message {} failed for topic = {}",
                        message.getId(), handler.topic(), ex);
                markFailed(message, ex);
            }
        }
        _log.info("dispatchPerMessage, Finished {} messages for topic = {}", messages.size(), handler.topic());
    }

    private void markProcessed(List<UserInbox> messages) {
        Instant processedAt = Instant.now();
        for (UserInbox message : messages) {
            message.setStatus(InboxStatus.PROCESSED);
            message.setProcessedAt(processedAt);
            message.setMessage(null);
        }
        this._inboxRepo.saveAll(messages);
    }

    private void markProcessed(UserInbox message) {
        message.setStatus(InboxStatus.PROCESSED);
        message.setProcessedAt(Instant.now());
        message.setMessage(null);
        this._inboxRepo.save(message);
    }

    private void markFailed(List<UserInbox> messages, Throwable ex) {
        try {
            this._txTemplate.executeWithoutResult(status -> {
                applyFailedStatus(messages, ex);
                this._inboxRepo.saveAll(messages);
            });
        } catch (Exception saveEx) {
            _log.error("markFailed, Failed to update inbox status", saveEx);
        }
    }

    private void markFailed(UserInbox message, Throwable ex) {
        try {
            this._txTemplate.executeWithoutResult(status -> {
                applyFailedStatus(List.of(message), ex);
                this._inboxRepo.save(message);
            });
        } catch (Exception saveEx) {
            _log.error("markFailed, Failed to update inbox status for message {}", message.getId(), saveEx);
        }
    }

    private void applyFailedStatus(List<UserInbox> messages, Throwable ex) {
        int maxRetries = this._userProperties.getInbox().getMaxRetries();
        String errorMessage = resolveErrorMessage(ex);
        Instant now = Instant.now();

        for (UserInbox message : messages) {
            int nextRetry = message.getRetry() == null ? 1 : message.getRetry() + 1;
            message.setRetry(nextRetry);
            message.setMessage(errorMessage);

            if (nextRetry > maxRetries) {
                message.setStatus(InboxStatus.FAILED);
                continue;
            }

            long delaySeconds = (long) Math.pow(2, nextRetry);
            message.setStatus(InboxStatus.RETRY);
            message.setNextRetryAt(now.plusSeconds(delaySeconds));
        }
    }

    private String resolveErrorMessage(Throwable ex) {
        if (ex == null || ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "Unexpected inbox processing error.";
        }
        return ex.getMessage().length() <= 500 ? ex.getMessage() : ex.getMessage().substring(0, 500);
    }
}
