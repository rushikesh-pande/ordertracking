package com.ordertracking.monitoring.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Monitoring Enhancement: Business Metrics Service
 *
 * Central place to record domain-level metrics for ordertracking.
 * These appear in Prometheus and can be visualised in Grafana.
 *
 * Metrics registered:
 *  - ordertracking.operations.total      — counter per operation + status
 *  - ordertracking.active.gauge          — current in-flight operations
 *  - ordertracking.operation.duration    — summary timer
 *  - ordertracking.errors.total          — error counter per error type
 *  - ordertracking.kafka.events.total    — Kafka event counter
 */
@Service
public class BusinessMetricsService {

    private final MeterRegistry meterRegistry;

    // Gauge — currently active operations
    private final AtomicInteger activeOperations = new AtomicInteger(0);

    public BusinessMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        // Register gauge once
        Gauge.builder("ordertracking.active.operations", activeOperations, AtomicInteger::get)
             .description("Number of currently active ordertracking operations")
             .tag("service", "ordertracking")
             .register(meterRegistry);
    }

    /**
     * Record a successful operation completion.
     * @param operationType  e.g. "create", "update", "delete", "query"
     */
    public void recordSuccess(String operationType) {
        Counter.builder("ordertracking.operations.total")
               .tag("service", "ordertracking")
               .tag("operation", operationType)
               .tag("status", "success")
               .description("Total ordertracking operations by type and status")
               .register(meterRegistry)
               .increment();
    }

    /**
     * Record a failed operation.
     * @param operationType  e.g. "create", "update"
     * @param errorType      e.g. "validation", "database", "kafka"
     */
    public void recordFailure(String operationType, String errorType) {
        Counter.builder("ordertracking.errors.total")
               .tag("service", "ordertracking")
               .tag("operation", operationType)
               .tag("error_type", errorType)
               .description("Total ordertracking errors by operation and error type")
               .register(meterRegistry)
               .increment();
    }

    /**
     * Record a Kafka event published or consumed.
     * @param topic      Kafka topic name
     * @param direction  "published" or "consumed"
     */
    public void recordKafkaEvent(String topic, String direction) {
        Counter.builder("ordertracking.kafka.events.total")
               .tag("service", "ordertracking")
               .tag("topic", topic)
               .tag("direction", direction)
               .description("Total Kafka events for ordertracking")
               .register(meterRegistry)
               .increment();
    }

    /**
     * Record operation duration.
     * @param operationType  operation name
     * @param durationMs     elapsed milliseconds
     */
    public void recordDuration(String operationType, long durationMs) {
        meterRegistry.summary("ordertracking.operation.duration",
                "service", "ordertracking",
                "operation", operationType)
               .record(durationMs);
    }

    /** Mark one more in-flight operation. Call at start of operation. */
    public void incrementActive() { activeOperations.incrementAndGet(); }

    /** Mark one less in-flight operation. Call in finally block. */
    public void decrementActive() { activeOperations.decrementAndGet(); }
}
