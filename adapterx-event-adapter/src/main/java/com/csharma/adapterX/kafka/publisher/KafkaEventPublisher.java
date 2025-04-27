package com.csharma.adapterX.kafka.publisher;

import com.csharma.adapterX.config.EventBrokerConfig;
import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;
import com.csharma.adapterX.message.serializer.EventSerializer;
import com.csharma.adapterX.publisher.EventPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class KafkaEventPublisher implements EventPublisher {

    private final KafkaProducer<String, byte[]> producer;
    private final EventSerializer serializer;

    public KafkaEventPublisher(EventBrokerConfig config, EventSerializer serializer) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBrokerHost() + ":" + config.getBrokerPort());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        // Add additional properties
        props.putAll(config.getAdditionalProperties());

        this.producer = new KafkaProducer<>(props);
        this.serializer = serializer;
    }

    @Override
    public <T extends Event> CompletableFuture<Void> publish(String destination, EventEnvelope<T> eventEnvelope) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        byte[] data = serializer.serialize(eventEnvelope);
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(destination, eventEnvelope.getEvent().getEventId(), data);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                future.completeExceptionally(exception);
            } else {
                future.complete(null);
            }
        });
        return future;
    }

    @Override
    public <T extends Event> CompletableFuture<Void> publish(String destination, T event) {
        Map<String, String> headers = new HashMap<>();
        headers.put("eventType", event.getEventType());
        return publish(destination, new EventEnvelope<>(event, headers));
    }
}
