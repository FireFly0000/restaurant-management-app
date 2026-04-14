package com.restaurant.authservice.core.service.authoutbox;

import com.restaurant.authservice.config.AuthServiceProperties;
import com.restaurant.authservice.core.repository.IAuthOutboxRepository;
import com.restaurant.commons.core.enums.OutboxStatus;
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
public class AuthOutboxWorker {
    private static final Logger _log = LoggerFactory.getLogger(AuthOutboxWorker.class);

    private final ThreadPoolTaskExecutor _executor;
    private final AuthOutboxProcessor _processor;
    private final AuthServiceProperties _authProperties;
    private final TransactionTemplate _transTemplate;
    private final IAuthOutboxRepository _repo;
    private final AtomicBoolean _isShuttingDown = new AtomicBoolean(false);

    public AuthOutboxWorker(
            @Qualifier("authOutboxExecutor") ThreadPoolTaskExecutor executor,
            AuthOutboxProcessor processor,
            AuthServiceProperties authProperties,
            TransactionTemplate transTemplate,
            IAuthOutboxRepository repo
    ) {
        this._executor = executor;
        this._processor = processor;
        this._authProperties = authProperties;
        this._transTemplate = transTemplate;
        this._repo = repo;
    }

    @PreDestroy
    public void onShutdown() {
        _log.warn("onShutdown, Shutting down AuthOutboxWorker");
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
                        this._repo.findBatchIdsToProcess(this._authProperties.getOutbox().getBatchSize());

                if (claimedMessageIds == null || claimedMessageIds.isEmpty()) {
                    return claimedMessageIds;
                }

                Instant leaseUntil = Instant.now()
                        .plusSeconds(this._authProperties.getOutbox().getLeaseSeconds());
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
                    .get(this._authProperties.getOutbox().getBatchTimeoutSeconds(), TimeUnit.SECONDS);
            _log.info("schedulePush, Completed processing {} outbox messages", messageIds.size());
        } catch (TimeoutException ex) {
            _log.error("schedulePush, Auth outbox batch processing timed out", ex);
        } catch (Exception ex) {
            _log.error("schedulePush, Failed to process auth outbox batch", ex);
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
