package com.csharma.adapterX.message.serializer;

import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;


/**
 * Event serialization interface for converting events to/from different formats
 */
public interface EventSerializer {
    <T extends Event> byte[] serialize(EventEnvelope<T> eventEnvelope);
    <T extends Event> EventEnvelope<T> deserialize(byte[] data, Class<T> eventType);
}
