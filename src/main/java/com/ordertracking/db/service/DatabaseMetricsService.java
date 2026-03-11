package com.ordertracking.db.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Database Optimisation Enhancement: Database Metrics Service
 *
 * Tracks cache and query performance metrics for ordertracking.
 * Exposed to Prometheus via /actuator/prometheus.
 *
 * Metrics:
 *  - ordertracking_cache_hits_total       — Redis cache hits
 *  - ordertracking_cache_misses_total     — Redis cache misses (DB queries)
 *  - ordertracking_db_queries_total       — Total DB queries by type
 *  - ordertracking_db_slow_queries_total  — Queries above 500ms
 *  - ordertracking_connection_pool_active — HikariCP active connections
 */
@Service
public class DatabaseMetricsService {

    private final MeterRegistry meterRegistry;
    private final AtomicLong activeConnections = new AtomicLong(0);

    public DatabaseMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("ordertracking.connection.pool.active", activeConnections, AtomicLong::get)
             .description("Active HikariCP connections for ordertracking")
             .tag("service", "ordertracking")
             .register(meterRegistry);
    }

    public void recordCacheHit(String cacheName) {
        Counter.builder("ordertracking.cache.hits.total")
               .tag("service", "ordertracking").tag("cache", cacheName)
               .description("Redis cache hits for ordertracking")
               .register(meterRegistry).increment();
    }

    public void recordCacheMiss(String cacheName) {
        Counter.builder("ordertracking.cache.misses.total")
               .tag("service", "ordertracking").tag("cache", cacheName)
               .description("Redis cache misses for ordertracking (DB fallback)")
               .register(meterRegistry).increment();
    }

    public void recordDbQuery(String queryType) {
        Counter.builder("ordertracking.db.queries.total")
               .tag("service", "ordertracking").tag("type", queryType)
               .description("DB queries for ordertracking")
               .register(meterRegistry).increment();
    }

    public void recordSlowQuery(String queryType, long ms) {
        Counter.builder("ordertracking.db.slow.queries.total")
               .tag("service", "ordertracking").tag("type", queryType)
               .description("DB queries exceeding 500ms for ordertracking")
               .register(meterRegistry).increment();
        meterRegistry.summary("ordertracking.db.query.duration",
                "service", "ordertracking", "type", queryType).record(ms);
    }

    public void setActiveConnections(long count) {
        activeConnections.set(count);
    }
}
