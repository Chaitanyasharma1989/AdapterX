package com.csharma.adapterX.subscriber;

import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;

import java.util.function.Consumer;

/**
 * Message broker abstraction for subscribing to events
 */
public interface EventSubscriber {
    <T extends Event> void subscribe(String source, Class<T> eventType, Consumer<EventEnvelope<T>> eventHandler);

    void unsubscribe(String source);
}
