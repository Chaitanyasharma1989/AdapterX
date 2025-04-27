package com.csharma.adapterX.eventhandler;


import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;

/**
 * Service for dispatching events to registered handlers
 */
public interface EventDispatcher {
    <T extends Event> void dispatch(EventEnvelope<T> eventEnvelope);
}
