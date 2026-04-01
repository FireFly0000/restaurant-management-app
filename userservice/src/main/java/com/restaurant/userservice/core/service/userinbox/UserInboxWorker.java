package com.restaurant.userservice.core.service.userinbox;

import com.restaurant.userservice.model.UserInbox;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class UserInboxWorker {
    private static final Logger _log = LoggerFactory.getLogger(UserInboxWorker.class);

    private final UserInboxService _userInboxService;
    private final UserInboxProcessor _userInboxProcessor;
    private final ThreadPoolTaskExecutor _executor;

    private final AtomicBoolean _isShuttingDown = new AtomicBoolean(false);

    public UserInboxWorker(
            UserInboxService userInboxService,
            UserInboxProcessor userInboxProcessor,
            @Qualifier("userInboxExecutor") ThreadPoolTaskExecutor executor
    ) {
        this._userInboxService = userInboxService;
        this._userInboxProcessor = userInboxProcessor;
        this._executor = executor;
    }

    @PreDestroy
    public void onShutdown() {
        _log.warn("onShutdown, Stop inbox worker because application is shutting down");
        this._isShuttingDown.set(true);
    }

    @Scheduled(fixedDelay = 500)
    public void scheduleProcess() {
        if (this._isShuttingDown.get()) {
            _log.debug("scheduleProcess, Skip inbox processing because application is shutting down");
            return;
        }

        List<UserInbox> messages = this._userInboxService.claimNextBatch();
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }

        Map<String, List<UserInbox>> messagesByKey = messages.stream()
                .collect(Collectors.groupingBy(
                        UserInbox::getMessageKey,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        _log.info("scheduleProcess, Dispatching {} messages in {} key-groups to thread pool",
                messages.size(), messagesByKey.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>(messagesByKey.size());

        for (Map.Entry<String, List<UserInbox>> entry : messagesByKey.entrySet()) {
            futures.add(CompletableFuture.runAsync(
                    () -> this._userInboxProcessor.processBatch(entry.getValue()),
                    this._executor
            ).exceptionally(ex -> {
                _log.error("scheduleProcess, Key-group [{}] failed unexpectedly", entry.getKey(), ex);
                return null;
            }));
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }
}
