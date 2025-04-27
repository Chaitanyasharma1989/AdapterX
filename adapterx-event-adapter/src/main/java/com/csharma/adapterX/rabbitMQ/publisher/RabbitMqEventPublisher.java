package com.csharma.adapterX.rabbitMQ.publisher;

import com.csharma.adapterX.config.EventBrokerConfig;
import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;
import com.csharma.adapterX.message.serializer.EventSerializer;
import com.csharma.adapterX.publisher.EventPublisher;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class RabbitMqEventPublisher implements EventPublisher {

    private final Channel channel;
    private final EventSerializer serializer;

    public RabbitMqEventPublisher(EventBrokerConfig config, EventSerializer serializer) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.getBrokerHost());
            factory.setPort(config.getBrokerPort());

            if (config.getUsername() != null && !config.getUsername().isEmpty()) {
                factory.setUsername(config.getUsername());
                factory.setPassword(config.getPassword());
            }

            Connection connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.serializer = serializer;
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to create RabbitMQ publisher", e);
        }
    }

    @Override
    public <T extends Event> CompletableFuture<Void> publish(String exchange, EventEnvelope<T> eventEnvelope) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            // Declare exchange if it doesn't exist
            channel.exchangeDeclare(exchange, "topic", true);
            // Create AMQP headers
            AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();

            Map<String, Object> headers = new HashMap<>(eventEnvelope.getHeaders());
            headers.put("eventId", eventEnvelope.getEvent().getEventId());
            headers.put("eventType", eventEnvelope.getEvent().getEventType());

            AMQP.BasicProperties props = propsBuilder
                    .headers(headers)
                    .contentType("application/json")
                    .build();

            // Serialize and publish
            byte[] data = serializer.serialize(eventEnvelope);
            String routingKey = eventEnvelope.getEvent().getEventType();

            channel.basicPublish(exchange, routingKey, props, data);
            future.complete(null);

        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public <T extends Event> CompletableFuture<Void> publish(String exchange, T event) {
        Map<String, String> headers = new HashMap<>();
        headers.put("eventType", event.getEventType());
        return publish(exchange, new EventEnvelope<>(event, headers));
    }
}

