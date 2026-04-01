package com.restaurant.businessservice.core.service.outbox;

import com.restaurant.businessservice.config.BusinessProperties;
import com.restaurant.businessservice.core.repository.IBusinessOutboxRepository;
import com.restaurant.businessservice.model.BusinessOutBox;
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
public class BusinessOutboxWorker {
    private static final Logger _log = LoggerFactory.getLogger(BusinessOutboxWorker.class);

    private final ThreadPoolTaskExecutor _executor;
    private final BusinessOutboxProcessor _processor;
    private final BusinessProperties _businessProperties;
    private final TransactionTemplate _transTemplate;
    private final IBusinessOutboxRepository _repo;
    private final AtomicBoolean _isShuttingDown = new AtomicBoolean(false);

    public BusinessOutboxWorker(
            @Qualifier("businessOutboxExecutor") ThreadPoolTaskExecutor executor,
            BusinessOutboxProcessor processor,
            BusinessProperties businessProperties,
            TransactionTemplate transTemplate,
            IBusinessOutboxRepository repo
    ) {
        this._executor = executor;
        this._processor = processor;
        this._businessProperties = businessProperties;
        this._transTemplate = transTemplate;
        this._repo = repo;
    }

    @PreDestroy
    public void onShutdown() {
        _log.warn("onShutdown, Shutting down BusinessOutboxWorker");
        this._isShuttingDown.set(true);
    }

    @Scheduled(fixedDelay = 500)
    public void schedulePush() {
        if (this._isShuttingDown.get()) {
            _log.debug("schedulePush, Skip processing because worker is shutting down");
            return;
        }

        try {
            // Claim only ids first so the scheduler does not pull the whole payload batch into memory.
            List<UUID> messageIds = this._transTemplate.execute(trans ->
            {
                List<UUID> claimedMessageIds =
                        this._repo.findBatchIdsToProcess(this._businessProperties.getOutbox().getBatchSize());

                if (claimedMessageIds == null || claimedMessageIds.isEmpty()) {
                    return claimedMessageIds;
                }

                Instant leaseUntil = Instant.now()
                        .plusSeconds(this._businessProperties.getOutbox().getLeaseSeconds());
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
                    .get(this._businessProperties.getOutbox().getBatchTimeoutSeconds(), TimeUnit.SECONDS);
            _log.info("schedulePush, Completed processing {} outbox messages", messageIds.size());
        } catch (TimeoutException ex) {
            _log.error("schedulePush, Business outbox batch processing timed out", ex);
        } catch (Exception ex) {
            _log.error("schedulePush, Failed to process business outbox batch", ex);
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
