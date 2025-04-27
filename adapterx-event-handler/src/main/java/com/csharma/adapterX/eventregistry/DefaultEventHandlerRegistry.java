package com.csharma.adapterX.eventregistry;

import com.csharma.adapterX.eventhandler.EventDispatcher;
import com.csharma.adapterX.eventhandler.EventHandler;
import com.csharma.adapterX.eventhandler.EventHandlerRegistry;
import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the event handler registry and dispatcher
 */
public class DefaultEventHandlerRegistry implements EventHandlerRegistry, EventDispatcher {

    private final Map<String, EventHandler<?>> handlers = new ConcurrentHashMap<>();

    @Override
    public <T extends Event> void registerHandler(EventHandler<T> handler) {
        handlers.put(handler.getEventType().getName(), handler);
    }

    @Override
    public <T extends Event> void unregisterHandler(Class<T> eventType) {
        handlers.remove(eventType.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatch(EventEnvelope<T> eventEnvelope) {
        T event = eventEnvelope.getEvent();
        EventHandler<T> handler = (EventHandler<T>) handlers.get(event.getClass().getName());

        if (handler != null) {
            handler.handle(eventEnvelope);
        } else {
            // Log warning about unhandled event
            System.out.println("No handler registered for event type: " + event.getClass().getName());
        }
    }
}
