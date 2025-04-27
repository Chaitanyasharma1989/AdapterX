package com.csharma.adapterX.config;

import java.util.Map;

/**
 * Configuration properties for event brokers
 */
public interface EventBrokerConfig {

    String getBrokerHost();

    int getBrokerPort();

    String getUsername();

    String getPassword();

    Map<String, String> getAdditionalProperties();
}
