package com.csharma.adapterX.message.event;

import java.util.Map;


/**
 * Event envelope that wraps the actual event data
 * This provides a consistent structure for all events
 */
public class EventEnvelope <T extends Event> {
    private final T event;
    private final Map<String, String> headers;

    public EventEnvelope(T event, Map<String, String> headers) {
        this.event = event;
        this.headers = headers;
    }

    public T getEvent() {
        return event;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
