package com.ordertracking.repository;

import com.ordertracking.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory, Long> {
    List<TrackingHistory> findByOrderIdOrderByEventTimeDesc(String orderId);
}
