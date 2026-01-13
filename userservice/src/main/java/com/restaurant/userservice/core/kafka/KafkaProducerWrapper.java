package com.restaurant.userservice.core.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerWrapper {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send the message to the topic
     * @param topic Topic
     * @param key Key
     * @param message Message
     */
    public void sendMessage(String topic, String key, Object message){
        log.debug("Preparing to send message to Topic: {}, Key: {}", topic, key);
        try{
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

            future.whenComplete((result, ex) -> {
                if(ex == null){
                    log.debug("Sent message successfully to Topic: {}, Partition: {}, Offset: {}",
                            topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }else{
                    log.error("Failed to send message to Topic: {}, Error: {}", topic, ex.getMessage());
                }
            });
        }catch (Exception e) {
            log.error("Exception during Kafka sending process", e);
        }
    }
}
