package com.csharma.adapterX.message.event;

import java.time.Instant;
import java.util.Map;


/**
 * Base interface for all events in the system
 */
public interface Event{
    String getEventId();
    String getEventType();
    Instant getTimestamp();
    String getSource();
    Map<String, Object> getMetadata();
}