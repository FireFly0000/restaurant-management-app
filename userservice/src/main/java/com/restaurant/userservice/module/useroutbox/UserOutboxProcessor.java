package com.restaurant.userservice.module.useroutbox;

import com.restaurant.commons.enums.OutboxStatus;
import com.restaurant.userservice.config.UserProperties;
import com.restaurant.userservice.core.kafka.KafkaProducerWrapper;
import com.restaurant.userservice.model.UserOutBox;
import com.restaurant.userservice.module.useroutbox.repository.UserOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserOutboxProcessor {
    private final UserOutboxRepository _repo;
    private final KafkaProducerWrapper _kafkaWrapper;
    private final UserProperties _userProperties;

    private final static Logger _log = LoggerFactory.getLogger(UserOutboxProcessor.class);

    public UserOutboxProcessor(
            UserOutboxRepository repo,
            KafkaProducerWrapper kafkaWrapper,
            UserProperties userProperties
    ){
        this._repo = repo;
        this._kafkaWrapper = kafkaWrapper;
        this._userProperties = userProperties;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processMessage(UserOutBox msg){
        try{
            if(msg.getPayload() == null || msg.getPayload().length == 0){
                _log.error("Message no have payload");
                return;
            }

            _kafkaWrapper.sendMessage(msg.getTopic(), msg.getKey(), msg.getPayload());

            msg.setStatus(OutboxStatus.SENT);
            msg.setMessage(null);
            _repo.save(msg);

            _log.debug("Successfully sent message {} to topic {}", msg.getId(), msg.getTopic());
        }catch (Exception ex){
            _log.error("Failed to process message: {}", msg.getId(), ex);
            throw ex;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(UserOutBox msg, Throwable ex) {
        UserOutBox entity = this._repo.findById(msg.getId()).orElse(null);
        if(entity != null && !entity.getStatus().equals(OutboxStatus.SENT)){
            entity.setStatus(OutboxStatus.FAILED);
            entity.setMessage(ex.getMessage());
            this._repo.save(entity);
            _log.error("Message {} marked as FAILED: {}", entity.getId(), ex.getMessage());
        }
    }

    private void handleFailure(UserOutBox msg, Throwable ex){
        _log.error("Failed to process message {}: {}", msg.getId(), ex.getMessage());
        int maxRetries = _userProperties.getOutbox().getMaxRetries();
        if(msg.getRetry() >= maxRetries) {
            msg.setStatus(OutboxStatus.FAILED);
            msg.setMessage(ex.getMessage());
            _log.warn("Message {} marked as FAILED after {} retries", msg.getId(), maxRetries);
        }else{
            int delaySeconds = (int) Math.pow(2, msg.getRetry() + 1);
            msg.setStatus(OutboxStatus.RETRY);
            msg.setNextRetryAt(Instant.now().plusSeconds(delaySeconds));
            msg.setRetry(msg.getRetry() + 1);
            _log.debug("Message {} scheduled for retry #{} in {}s",
                    msg.getId(), msg.getRetry(), delaySeconds);
        }

        _repo.save(msg);
    }
}
