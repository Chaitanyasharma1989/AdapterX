package com.csharma.adapterX.publisher;

import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;

import java.util.concurrent.CompletableFuture;

/**
 * Message broker abstraction for publishing events
 */
public interface EventPublisher {
    <T extends Event> CompletableFuture<Void> publish(String destination, EventEnvelope<T> eventEnvelope);
    <T extends Event> CompletableFuture<Void> publish(String destination, T event);
}
