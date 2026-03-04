package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Exposes Spring Boot Actuator metrics endpoint for performance monitoring.
 * Metrics include: http.server.requests, jvm.memory, process.cpu.usage
 *
 * Access via: GET /actuator/metrics
 *             GET /actuator/metrics/http.server.requests
 *
 * Auto-added by CodeGen Agent to: ordertracking
 */
@Configuration
public class ActuatorConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "ordertracking", "team", "order-processing");
    }
}
