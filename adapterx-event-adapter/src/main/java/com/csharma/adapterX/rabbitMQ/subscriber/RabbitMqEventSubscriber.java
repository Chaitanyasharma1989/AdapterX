package com.csharma.adapterX.rabbitMQ.subscriber;

import com.csharma.adapterX.config.EventBrokerConfig;
import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;
import com.csharma.adapterX.message.serializer.EventSerializer;
import com.csharma.adapterX.subscriber.EventSubscriber;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitMqEventSubscriber implements EventSubscriber {

    private final Connection connection;
    private final EventSerializer serializer;
    private final Map<String, SubscriptionInfo> subscriptions = new ConcurrentHashMap<>();

    public RabbitMqEventSubscriber(EventBrokerConfig config, EventSerializer serializer) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.getBrokerHost());
            factory.setPort(config.getBrokerPort());

            if (config.getUsername() != null && !config.getUsername().isEmpty()) {
                factory.setUsername(config.getUsername());
                factory.setPassword(config.getPassword());
            }

            this.connection = factory.newConnection();
            this.serializer = serializer;
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to create RabbitMQ subscriber", e);
        }
    }

    @Override
    public <T extends Event> void subscribe(String exchange, Class<T> eventType, Consumer<EventEnvelope<T>> eventHandler) {
        try {
            Channel channel = connection.createChannel();
            // Declare exchange if it doesn't exist
            channel.exchangeDeclare(exchange, "topic", true);
            // Create a queue and bind it to the exchange
            String queueName = channel.queueDeclare().getQueue();
            String routingKey = "#"; // Subscribe to all events in this exchange
            channel.queueBind(queueName, exchange, routingKey);
            // Create consumer
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    EventEnvelope<T> eventEnvelope = serializer.deserialize(delivery.getBody(), eventType);
                    eventHandler.accept(eventEnvelope);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception e) {
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                    e.printStackTrace();
                }
            };

            CancelCallback cancelCallback = consumerTag -> {
                // Handle cancellation
            };

            String consumerTag = channel.basicConsume(queueName, false, deliverCallback, cancelCallback);
            // Store subscription info for cleanup
            subscriptions.put(exchange, new SubscriptionInfo(channel, consumerTag));
        } catch (IOException e) {
            throw new RuntimeException("Failed to subscribe to exchange: " + exchange, e);
        }
    }

    @Override
    public void unsubscribe(String exchange) {
        SubscriptionInfo info = subscriptions.remove(exchange);
        if (info != null) {
            try {
                info.channel.basicCancel(info.consumerTag);
                info.channel.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private static class SubscriptionInfo {
        private final Channel channel;
        private final String consumerTag;

        public SubscriptionInfo(Channel channel, String consumerTag) {
            this.channel = channel;
            this.consumerTag = consumerTag;
        }
    }
}
