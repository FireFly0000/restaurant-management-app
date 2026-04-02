package com.restaurant.userservice.core.service.useroutbox;

import com.restaurant.commons.core.enums.OutboxStatus;
import com.restaurant.userservice.config.UserProperties;
import com.restaurant.userservice.core.repository.IUserOutboxRepository;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class UserOutboxWorker {
    private static final Logger _log = LoggerFactory.getLogger(UserOutboxWorker.class);

    private final ThreadPoolTaskExecutor _executor;
    private final UserOutboxProcessor _processor;
    private final UserProperties _userProperties;
    private final TransactionTemplate _transTemplate;
    private final IUserOutboxRepository _repo;
    private final AtomicBoolean _isShuttingDown = new AtomicBoolean(false);

    public UserOutboxWorker(
            @Qualifier("userOutboxExecutor") ThreadPoolTaskExecutor executor,
            UserOutboxProcessor processor,
            UserProperties userProperties,
            TransactionTemplate transTemplate,
            IUserOutboxRepository repo
    ) {
        this._executor = executor;
        this._processor = processor;
        this._userProperties = userProperties;
        this._transTemplate = transTemplate;
        this._repo = repo;
    }

    @PreDestroy
    public void onShutdown() {
        _log.warn("onShutdown, Shutting down UserOutboxWorker");
        this._isShuttingDown.set(true);
    }

    @Scheduled(fixedDelay = 500)
    public void schedulePush() {
        if (this._isShuttingDown.get()) {
            _log.debug("schedulePush, Skip processing because worker is shutting down");
            return;
        }

        try {
            List<UUID> messageIds = this._transTemplate.execute(trans -> {
                List<UUID> claimedMessageIds =
                        this._repo.findBatchIdsToProcess(this._userProperties.getOutbox().getBatchSize());

                if (claimedMessageIds == null || claimedMessageIds.isEmpty()) {
                    return claimedMessageIds;
                }

                Instant leaseUntil = Instant.now()
                        .plusSeconds(this._userProperties.getOutbox().getLeaseSeconds());
                this._repo.claimBatch(claimedMessageIds, OutboxStatus.PROCESSING.name(), leaseUntil);
                return claimedMessageIds;
            });

            if (messageIds == null || messageIds.isEmpty()) {
                _log.debug("schedulePush, No outbox message to process");
                return;
            }

            _log.info("schedulePush, Start process {} outbox messages", messageIds.size());

            List<CompletableFuture<Void>> futures = messageIds.stream()
                    .map(messageId -> CompletableFuture.runAsync(
                            () -> processWithErrorHandling(messageId),
                            this._executor
                    ))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(this._userProperties.getOutbox().getBatchTimeoutSeconds(), TimeUnit.SECONDS);
            _log.info("schedulePush, Completed processing {} outbox messages", messageIds.size());
        } catch (TimeoutException ex) {
            _log.error("schedulePush, User outbox batch processing timed out", ex);
        } catch (Exception ex) {
            _log.error("schedulePush, Failed to process user outbox batch", ex);
        }
    }

    private void processWithErrorHandling(UUID messageId) {
        try {
            this._processor.processMessage(messageId);
        } catch (Exception ex) {
            _log.error("processWithErrorHandling, Failed to process message id = {}", messageId, ex);
            try {
                this._processor.handleFailure(messageId, ex);
            } catch (Exception fallback) {
                _log.error("processWithErrorHandling, Failed to update outbox state for message id = {}", messageId, fallback);
            }
        }
    }
}
