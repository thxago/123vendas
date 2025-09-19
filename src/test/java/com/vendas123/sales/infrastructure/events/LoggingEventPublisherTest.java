package com.vendas123.sales.infrastructure.events;

import org.junit.jupiter.api.Test;

class LoggingEventPublisherTest {
    @Test
    void publish_does_not_throw() {
        LoggingEventPublisher pub = new LoggingEventPublisher();
        pub.publish("TestEvent", java.util.Map.of("k","v"));
        // No assertion needed; just ensure no exception
    }
}
