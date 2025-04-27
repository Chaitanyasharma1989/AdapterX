package com.csharma.adapterX.message.serializer;
import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;
import com.csharma.adapterX.message.exception.SerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class JsonEventSerializer implements EventSerializer {

    private final ObjectMapper objectMapper;

    public JsonEventSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public <T extends Event> byte[] serialize(EventEnvelope<T> eventEnvelope) {
        try {
            return objectMapper.writeValueAsBytes(eventEnvelope);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize event", e);
        }
    }

    @Override
    public <T extends Event> EventEnvelope<T> deserialize(byte[] data, Class<T> eventType) {
        try {
            return objectMapper.readValue(data, objectMapper.getTypeFactory()
                    .constructParametricType(EventEnvelope.class, eventType));
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize event", e);
        }
    }
}
