package com.restaurant.notification.core.service.sender;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.notification.core.service.sender.dto.AbstractNotificationPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractNotificationSender<T extends AbstractNotificationPayload> implements INotificationSender<T> {

    private static final Logger _log = LoggerFactory.getLogger(AbstractNotificationSender.class);

    @Override
    public abstract NotiChannel getChannel();

    @Override
    public boolean validate(T payload) {
        if(payload == null){
            _log.error("validate, Payload is null");
            return false;
        }

        return true;
    }

    @Override
    public abstract SendResult send(T payload);

    @Override
    public List<SendResult> sendBulk(List<T> payloads) {
        _log.info("sendBulk, [{}] Fallback to loop sending for {} payloads", getChannel(), payloads.size());
        return payloads.stream()
                .map(payload -> {
                    try{
                        return this.send(payload);
                    }catch (Exception e){
                        _log.error("sendBulk, [{}] Error sending in fallback,  {}", getChannel().name(), e.getMessage());
                        return SendResult.failure("Unknown", getChannel().name(), payload.getEventId() ,e.getMessage(), Constant.RES0006);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<SendResult> sendBulkScheduled(List<T> payloads, LocalDateTime sendAt) {
        throw new UnsupportedOperationException(
                "Scheduled sending is not supported for channel: " + getChannel()
        );
    }

    @Override
    public SendResult sendScheduled(T payload, LocalDateTime sendAt) {
        throw new UnsupportedOperationException(
                "Scheduled sending is not supported for channel: " + getChannel()
        );
    }
}
