package com.restaurant.storageservice.core.kafka;

import com.restaurant.commons.core.rpc.storage.DeleteFileEvent;
import com.restaurant.storageservice.core.service.storage.IStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StorageEventHandler {
    private static final Logger _log = LoggerFactory.getLogger(StorageEventHandler.class);

    private final IStorageService _storageService;

    public StorageEventHandler(IStorageService storageService) {
        this._storageService = storageService;
    }

    @KafkaListener(topics = "storage.event.delete_file", containerFactory = "kafkaListenerContainerFactory", concurrency = "${app.notification.kafka.consumer.concurency}")
    public void handleStorageDeleteFileEvent(@Payload List<DeleteFileEvent> events, @Header("X-Event-Id") List<String> eventIds, Acknowledgment ack){
        if(events == null || events.isEmpty()){
            ack.acknowledge();
            return;
        }
        _log.info("handleStorageDeleteFileEvent, Prepare to delete {} files", events.size());
        try{
            Map<String, List<String>> fileByBucket = events.stream()
                    .collect(Collectors.groupingBy(
                       DeleteFileEvent::getBucketName,
                       Collectors.mapping(DeleteFileEvent::getObjectName, Collectors.toList())
                    ));
            List<CompletableFuture<Void>> futures = fileByBucket.entrySet().stream()
                    .map(entry -> CompletableFuture.runAsync(() -> {
                        String bucketName = entry.getKey();
                        List<String> objects = entry.getValue();

                        boolean isDeleted = _storageService.deleteFiles(bucketName, objects);
                        if(!isDeleted){
                            _log.error("handleStorageDeleteFileEvent, Delete failed let retry!, bucketName: {}, objects size: {}", bucketName, objects.size());
                            // Throw to catch
                            throw new RuntimeException();
                        }
                    })).toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            ack.acknowledge();
            _log.info("handleStorageDeleteFileEvent, Deleted {} files", events.size());
        }catch (Exception e){
            _log.error("handleStorageDeleteFileEvent, Error during delete files, wait for Kafka retry: {}", e.getMessage());
            throw new RuntimeException("handleStorageDeleteFileEvent");
        }
    }
}
