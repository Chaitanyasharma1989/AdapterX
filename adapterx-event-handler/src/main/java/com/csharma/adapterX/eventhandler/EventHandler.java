package com.csharma.adapterX.eventhandler;

import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;

/**
 * Event handler interface for processing events
 */
public interface EventHandler<T extends Event> {
    void handle(EventEnvelope<T> eventEnvelope);

    Class<T> getEventType();
}