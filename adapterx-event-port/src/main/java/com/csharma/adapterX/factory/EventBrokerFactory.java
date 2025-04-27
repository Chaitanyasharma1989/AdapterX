package com.csharma.adapterX.factory;


import com.csharma.adapterX.publisher.EventPublisher;
import com.csharma.adapterX.subscriber.EventSubscriber;

/**
 * Factory for creating event publisher and subscriber instances
 */
public interface EventBrokerFactory {
    EventPublisher createPublisher();
    EventSubscriber createSubscriber();
}
