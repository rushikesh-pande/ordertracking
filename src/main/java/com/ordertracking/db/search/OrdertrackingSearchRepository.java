package com.ordertracking.db.search;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Database Optimisation Enhancement: Elasticsearch Search Repository
 * Provides full-text search over ordertracking entities.
 */
@Repository
public interface OrdertrackingSearchRepository
        extends ElasticsearchRepository<OrdertrackingSearchDocument, String> {

    List<OrdertrackingSearchDocument> findByNameContainingOrDescriptionContaining(
            String name, String description);

    List<OrdertrackingSearchDocument> findByStatus(String status);

    List<OrdertrackingSearchDocument> findByCategory(String category);

    @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"name^2\",\"description\",\"category\"],\"fuzziness\":\"AUTO\",\"type\":\"best_fields\"}}")
    List<OrdertrackingSearchDocument> fuzzySearch(String query);
}
