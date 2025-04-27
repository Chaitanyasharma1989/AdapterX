package com.csharma.adapterX.eventservice;

import com.csharma.adapterX.eventhandler.EventDispatcher;
import com.csharma.adapterX.eventhandler.EventHandler;
import com.csharma.adapterX.eventhandler.EventHandlerRegistry;
import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.publisher.EventPublisher;
import com.csharma.adapterX.subscriber.EventSubscriber;

public class EventService {

    private final EventDispatcher eventDispatcher;
    private final EventHandlerRegistry eventHandlerRegistry;
    private final EventPublisher eventPublisher;
    private final EventSubscriber eventSubscriber;

    public EventService(final EventDispatcher eventDispatcher, final EventHandlerRegistry eventHandlerRegistry,
                        final EventPublisher eventPublisher, final EventSubscriber eventSubscriber) {
        this.eventDispatcher = eventDispatcher;
        this.eventHandlerRegistry = eventHandlerRegistry;
        this.eventPublisher = eventPublisher;
        this.eventSubscriber = eventSubscriber;
    }

    public <T extends Event> void publish(final String destination, final T event) {
        eventPublisher.publish(destination, event);
    }

    public <T extends Event> void registerHandler(final EventHandler<T> handler, final String source) {
        eventHandlerRegistry.registerHandler(handler);
        // Subscribe to the source and dispatch events to the handler
        eventSubscriber.subscribe(source, handler.getEventType(), eventDispatcher::dispatch);
    }

    public <T extends Event> void unregisterHandler(final Class<T> eventType, final String source) {
        eventHandlerRegistry.unregisterHandler(eventType);
        eventSubscriber.unsubscribe(source);
    }
}
