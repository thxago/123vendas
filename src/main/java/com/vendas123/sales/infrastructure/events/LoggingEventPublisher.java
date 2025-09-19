package com.vendas123.sales.infrastructure.events;

import com.vendas123.sales.domain.ports.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    @Override
    public void publish(String eventName, Object payload) {
        // Simulate event publication: log with a clear structure
        log.info("domain_event name={} payload={}", eventName, payload);
    }
}
