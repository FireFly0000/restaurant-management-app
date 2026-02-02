package com.restaurant.userservice.core.service.useroutbox;

import com.restaurant.userservice.config.UserProperties;
import com.restaurant.userservice.model.UserOutBox;
import com.restaurant.userservice.core.repository.IUserOutboxRepository;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class UserOutboxWorker {

    private final Executor _executor;
    private final UserOutboxProcessor _processor;
    private final UserProperties _userProperties;
    private final TransactionTemplate _transTemplate;
    private final IUserOutboxRepository _repo;

    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private final static Logger _log = LoggerFactory.getLogger(UserOutboxWorker.class);

    public UserOutboxWorker(
            @Qualifier("userOutboxExecutor") Executor executor,
            UserOutboxProcessor processor,
            UserProperties userProperties,
            TransactionTemplate transTemplate,
            IUserOutboxRepository repo
    ){
        this._executor = executor;
        this._processor = processor;
        this._userProperties = userProperties;
        this._transTemplate = transTemplate;
        this._repo = repo;
    }

    @PreDestroy
    public void onShutDown(){
        _log.warn("Shutting down UserOutboxWorker");
        isShuttingDown.set(true);
    }

    @Scheduled(fixedDelay = 500) // 0.5s
    public void schedulePush(){
        if(isShuttingDown.get()){
            _log.debug("Skipping outbox processing - shutting down");
            return;
        }

        try{
            List<UserOutBox> messages = this._transTemplate.execute(trans ->
                    this._repo.findBatchToProcess(this._userProperties.getOutbox().getBatchSize()));

            if(messages == null || messages.isEmpty()){
                _log.debug("No messages to process");
                return;
            }

            _log.info("Processing {} user outbox messages", messages.size());

            List<CompletableFuture<Void>> futures = messages.stream()
                    .map(msg -> CompletableFuture.runAsync(
                            () -> processWithErrorHandling(msg),
                            _executor
                    ))
                    .toList();

            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .get(30, TimeUnit.SECONDS); // Timeout sau 30s
            } catch (TimeoutException e) {
                _log.error("Batch processing timeout - some messages may still be processing");
            }
        }catch (Exception ex){
            _log.error("Error in schedulePush", ex);
        }
    }

    private void processWithErrorHandling(UserOutBox msg){
        try{
            this._processor.processMessage(msg);
        }catch (Exception ex){
            try{
                this._processor.markAsFailed(msg, ex);
            }catch (Exception fallback){
                _log.error("Failed to mark message {} as FAILED", msg.getId(), fallback);
            }
        }
    }
}
