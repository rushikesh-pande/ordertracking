package com.ordertracking.repository;

import com.ordertracking.entity.OrderTracking;
import com.ordertracking.entity.TrackingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    Optional<OrderTracking> findByOrderId(String orderId);
    List<OrderTracking> findByCustomerId(String customerId);
    List<OrderTracking> findByStatus(TrackingStatus status);
}
