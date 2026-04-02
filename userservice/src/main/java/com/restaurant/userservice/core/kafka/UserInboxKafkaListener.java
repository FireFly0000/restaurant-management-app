package com.restaurant.userservice.core.kafka;

import com.google.protobuf.Message;
import com.restaurant.commons.constant.KafkaTopic;
import com.restaurant.userservice.core.service.userinbox.UserInboxService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class UserInboxKafkaListener {
    private static final Logger _log = LoggerFactory.getLogger(UserInboxKafkaListener.class);
    private static final String LOCATION_MANAGER_UPDATED_CONSUMER = "user-location-manager-updated-consumer";

    private final UserInboxService _userInboxService;

    public UserInboxKafkaListener(UserInboxService userInboxService) {
        this._userInboxService = userInboxService;
    }

    @KafkaListener(
            topics = KafkaTopic.LOCATION_MANAGER_UPDATED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenUpdatedLocationManagerEvent(
            List<ConsumerRecord<String, Message>> records,
            Acknowledgment acknowledgment
    ) {
        if (CollectionUtils.isEmpty(records)) {
            acknowledgment.acknowledge();
            _log.debug("listenUpdatedLocationManagerEvent, No records received");
            return;
        }

        _log.info("listenUpdatedLocationManagerEvent, Received {} records", records.size());
        this._userInboxService.enqueue(LOCATION_MANAGER_UPDATED_CONSUMER, records);
        acknowledgment.acknowledge();
        _log.info("listenUpdatedLocationManagerEvent, Acknowledged {} records", records.size());
    }
}
