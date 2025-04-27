package com.csharma.adapterX.eventhandler;

import com.csharma.adapterX.message.event.Event;

/**
 * Registry for event handlers
 */
public interface EventHandlerRegistry {
    <T extends Event> void registerHandler(EventHandler<T> handler);

    <T extends Event> void unregisterHandler(Class<T> eventType);
}
