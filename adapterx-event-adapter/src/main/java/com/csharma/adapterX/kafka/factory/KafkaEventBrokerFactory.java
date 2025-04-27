package com.csharma.adapterX.kafka.factory;


import com.csharma.adapterX.config.EventBrokerConfig;
import com.csharma.adapterX.factory.EventBrokerFactory;
import com.csharma.adapterX.kafka.publisher.KafkaEventPublisher;
import com.csharma.adapterX.kafka.subscriber.KafkaEventSubscriber;
import com.csharma.adapterX.message.serializer.EventSerializer;
import com.csharma.adapterX.publisher.EventPublisher;
import com.csharma.adapterX.subscriber.EventSubscriber;

/**
 * Apache Kafka implementation
 */
public class KafkaEventBrokerFactory implements EventBrokerFactory {

    private final EventBrokerConfig config;
    private final EventSerializer serializer;

    public KafkaEventBrokerFactory(EventBrokerConfig config, EventSerializer serializer) {
        this.config = config;
        this.serializer = serializer;
    }

    @Override
    public EventPublisher createPublisher() {
        return new KafkaEventPublisher(config, serializer);
    }

    @Override
    public EventSubscriber createSubscriber() {
        return new KafkaEventSubscriber(config, serializer);
    }
}
