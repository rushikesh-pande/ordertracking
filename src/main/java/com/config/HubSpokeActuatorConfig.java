package com.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-added by Hub-Spoke Orchestrator v2.0 — ordertracking
 */
@Configuration
public class HubSpokeActuatorConfig {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> hubSpokeMetrics() {
        return reg -> reg.config().commonTags(
            "application", "ordertracking",
            "discovered_by", "hub_spoke_orchestrator_v2",
            "discovery_method", "github_api_keyword_score"
        );
    }
}
