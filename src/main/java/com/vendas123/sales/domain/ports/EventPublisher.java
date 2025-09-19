package com.vendas123.sales.domain.ports;

public interface EventPublisher {
    /**
     * Publishes a domain event with a simple name and an arbitrary payload object.
     * Implementations may log, send to a message broker, etc.
     */
    void publish(String eventName, Object payload);
}
