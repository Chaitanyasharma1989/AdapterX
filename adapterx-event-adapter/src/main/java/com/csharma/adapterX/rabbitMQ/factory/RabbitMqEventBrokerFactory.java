package com.csharma.adapterX.rabbitMQ.factory;


import com.csharma.adapterX.config.EventBrokerConfig;
import com.csharma.adapterX.factory.EventBrokerFactory;
import com.csharma.adapterX.message.serializer.EventSerializer;
import com.csharma.adapterX.publisher.EventPublisher;
import com.csharma.adapterX.rabbitMQ.publisher.RabbitMqEventPublisher;
import com.csharma.adapterX.rabbitMQ.subscriber.RabbitMqEventSubscriber;
import com.csharma.adapterX.subscriber.EventSubscriber;

/**
 * Apache Kafka implementation
 */
public class RabbitMqEventBrokerFactory implements EventBrokerFactory {

    private final EventBrokerConfig config;
    private final EventSerializer serializer;

    public RabbitMqEventBrokerFactory(EventBrokerConfig config, EventSerializer serializer) {
        this.config = config;
        this.serializer = serializer;
    }

    @Override
    public EventPublisher createPublisher() {
        return new RabbitMqEventPublisher(config, serializer);
    }

    @Override
    public EventSubscriber createSubscriber() {
        return new RabbitMqEventSubscriber(config, serializer);
    }
}
