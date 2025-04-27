package com.csharma.adapterX.kafka.subscriber;

import com.csharma.adapterX.config.EventBrokerConfig;
import com.csharma.adapterX.message.event.Event;
import com.csharma.adapterX.message.event.EventEnvelope;
import com.csharma.adapterX.message.serializer.EventSerializer;
import com.csharma.adapterX.subscriber.EventSubscriber;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class KafkaEventSubscriber implements EventSubscriber {

    private final EventBrokerConfig config;
    private final EventSerializer serializer;
    private final ExecutorService executorService;
    private final Map<String, ConsumerTask<?>> consumerTasks;

    public KafkaEventSubscriber(EventBrokerConfig config, EventSerializer serializer) {
        this.config = config;
        this.serializer = serializer;
        this.executorService = Executors.newCachedThreadPool();
        this.consumerTasks = new ConcurrentHashMap<>();
    }

    @Override
    public <T extends Event> void subscribe(String source, Class<T> eventType, Consumer<EventEnvelope<T>> eventHandler) {
        ConsumerTask<T> task = new ConsumerTask<>(config, source, eventType, serializer, eventHandler);
        consumerTasks.put(source, task);
        executorService.submit(task);
    }

    @Override
    public void unsubscribe(String source) {
        ConsumerTask<?> task = consumerTasks.remove(source);
        if (task != null) {
            task.stop();
        }
    }

    private static class ConsumerTask<T extends Event> implements Runnable {
        private final KafkaConsumer<String, byte[]> consumer;
        private final String topic;
        private final Class<T> eventType;
        private final EventSerializer serializer;
        private final Consumer<EventEnvelope<T>> eventHandler;
        private volatile boolean running = true;

        public ConsumerTask(final EventBrokerConfig config, final String topic, final Class<T> eventType,
                            final EventSerializer serializer, final Consumer<EventEnvelope<T>> eventHandler) {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBrokerHost() + ":" + config.getBrokerPort());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-consumer-group");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // Add additional properties
            props.putAll(config.getAdditionalProperties());

            this.consumer = new KafkaConsumer<>(props);
            this.topic = topic;
            this.eventType = eventType;
            this.serializer = serializer;
            this.eventHandler = eventHandler;
        }

        @Override
        public void run() {
            consumer.subscribe(Collections.singletonList(topic));

            while (running) {
                try {
                    consumer.poll(Duration.ofMillis(100)).forEach(this::processRecord);
                } catch (Exception e) {
                    // Log error but continue polling
                    e.printStackTrace();
                }
            }

            consumer.close();
        }

        private void processRecord(ConsumerRecord<String, byte[]> record) {
            try {
                EventEnvelope<T> eventEnvelope = serializer.deserialize(record.value(), eventType);
                eventHandler.accept(eventEnvelope);
            } catch (Exception e) {
                // Log error but continue processing other records
                e.printStackTrace();
            }
        }

        public void stop() {
            running = false;
        }
    }
}
