package com.csharma.adapterX.message.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Base implementation for all events
 */
public class BaseEvent implements Event{
    private final String eventId;
    private final String eventType;
    private final Instant timestamp;
    private final String source;
    private final Map<String, Object> metadata;

    protected BaseEvent(String eventType, String source, Map<String, Object> metadata) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = Instant.now();
        this.source = source;
        this.metadata = metadata;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
