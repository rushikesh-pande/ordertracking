package com.ordertracking.db.search;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Database Optimisation Enhancement: Search Service
 *
 * Combines Redis caching with Elasticsearch full-text search.
 * Search results are cached for 60s to reduce ES query load.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrdertrackingSearchService {

    private final OrdertrackingSearchRepository searchRepository;

    /**
     * Full-text search with Redis caching.
     * Cache key includes the query string — evict on data changes.
     */
    @Cacheable(value = "ordertrackingCache", key = "'search:' + #query")
    @Timed(value = "ordertracking.search.duration", description = "ES search duration")
    public List<OrdertrackingSearchDocument> search(String query) {
        log.info("[SEARCH] Full-text search for '{}'", query);
        return searchRepository.fuzzySearch(query);
    }

    @Cacheable(value = "ordertrackingCache", key = "'status:' + #status")
    public List<OrdertrackingSearchDocument> findByStatus(String status) {
        log.info("[SEARCH] Status filter: '{}'", status);
        return searchRepository.findByStatus(status);
    }

    /** Index a new document (call from Kafka consumer or service layer) */
    public OrdertrackingSearchDocument index(OrdertrackingSearchDocument doc) {
        log.info("[ES INDEX] Indexing {} id={}", "ordertracking", doc.getId());
        return searchRepository.save(doc);
    }

    /** Remove from index (call on delete/cancel) */
    public void removeFromIndex(String id) {
        log.info("[ES DELETE] Removing {} id={}", "ordertracking", id);
        searchRepository.deleteById(id);
    }
}
