package com.restaurant.commons.core.service.inbox;

import com.google.protobuf.Message;

import java.util.List;

/**
 * Contract for typed inbox event handlers.
 * Each implementation handles a single topic and chooses its processing mode:
 * <ul>
 *   <li>Batch: override {@link #handleBatch(List)} — all events processed together, 1 fail = all retry.
 *       Best for pure DB operations.</li>
 *   <li>Per-message: override {@link #handle(Object)} — each event processed individually, 1 fail = only that retry.
 *       Required for side effects (email, external API).</li>
 * </ul>
 *
 * @param <T> the protobuf event type this handler processes
 */
public interface InboxEventHandler<T extends Message> {

    String topic();

    T parse(byte[] payload);

    default boolean isBatchHandler() {
        return false;
    }

    default void handle(T event) {
        throw new UnsupportedOperationException("Per-message handle not implemented for topic: " + topic());
    }

    default void handleBatch(List<T> events) {
        throw new UnsupportedOperationException("Batch handle not implemented for topic: " + topic());
    }
}
