package com.restaurant.userservice.core.kafka.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.constant.KafkaTopic;
import com.restaurant.commons.core.enums.UserType;
import com.restaurant.commons.core.rpc.business.UpdatedLocationManagerEvent;
import com.restaurant.commons.core.service.inbox.InboxEventHandler;
import com.restaurant.commons.exception.AppException;
import com.restaurant.userservice.core.repository.IUserRepository;
import com.restaurant.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UpdatedLocationManagerInboxHandler implements InboxEventHandler<UpdatedLocationManagerEvent> {
    private static final Logger _log = LoggerFactory.getLogger(UpdatedLocationManagerInboxHandler.class);

    private final IUserRepository _userRepo;

    public UpdatedLocationManagerInboxHandler(IUserRepository userRepo) {
        this._userRepo = userRepo;
    }

    @Override
    public String topic() {
        return KafkaTopic.LOCATION_MANAGER_UPDATED;
    }

    @Override
    public boolean isBatchHandler() {
        return true;
    }

    @Override
    public UpdatedLocationManagerEvent parse(byte[] payload) {
        try {
            return UpdatedLocationManagerEvent.parseFrom(payload);
        } catch (InvalidProtocolBufferException ex) {
            throw new AppException(
                    "Failed to parse UpdatedLocationManagerEvent",
                    Constant.RES0005,
                    "500",
                    ex
            );
        }
    }

    @Override
    public void handleBatch(List<UpdatedLocationManagerEvent> events) {
        _log.info("handle, Processing {} UpdatedLocationManagerEvent(s)", events.size());

        Map<UUID, UserType> desiredUserTypes = new LinkedHashMap<>();
        for (UpdatedLocationManagerEvent event : events) {
            UUID newManagerId = parseUuid(event.getNewManagerId(), "newManagerId");
            desiredUserTypes.put(newManagerId, UserType.BUSINESS);

            String oldManagerIdValue = event.getOldManagerId();
            if (oldManagerIdValue == null || oldManagerIdValue.isBlank()) {
                continue;
            }

            UUID oldManagerId = parseUuid(oldManagerIdValue, "oldManagerId");
            if (!event.getOldManagerHasOtherLocations() && !oldManagerId.equals(newManagerId)) {
                desiredUserTypes.put(oldManagerId, UserType.CUSTOMER);
            }
        }

        if (desiredUserTypes.isEmpty()) {
            _log.info("handle, No user type changes required");
            return;
        }

        List<User> users = this._userRepo.getUsersByIds(new ArrayList<>(desiredUserTypes.keySet()));
        Map<UUID, User> usersById = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<User> usersToUpdate = new ArrayList<>();
        for (Map.Entry<UUID, UserType> entry : desiredUserTypes.entrySet()) {
            User user = usersById.get(entry.getKey());
            if (user == null) {
                _log.warn("handle, User not found with id = {}", entry.getKey());
                continue;
            }

            if (entry.getValue().equals(user.getUserType())) {
                continue;
            }

            user.setUserType(entry.getValue());
            usersToUpdate.add(user);
        }

        if (usersToUpdate.isEmpty()) {
            _log.info("handle, Users already have expected type");
            return;
        }

        this._userRepo.saveAll(usersToUpdate);
        _log.info("handle, Updated {} user(s)", usersToUpdate.size());
    }

    private UUID parseUuid(String value, String fieldName) {
        try {
            return UUID.fromString(value);
        } catch (Exception ex) {
            throw new AppException(
                    String.format("Invalid UUID in field %s: %s", fieldName, value),
                    Constant.RES3004,
                    "400",
                    ex
            );
        }
    }
}
