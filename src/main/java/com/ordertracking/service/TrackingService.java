package com.ordertracking.service;

import com.ordertracking.dto.*;
import com.ordertracking.entity.*;
import com.ordertracking.kafka.TrackingEventProducer;
import com.ordertracking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final OrderTrackingRepository trackingRepo;
    private final TrackingHistoryRepository historyRepo;
    private final TrackingEventProducer eventProducer;
    private final NotificationService notificationService;

    @Transactional
    public TrackingResponse createTracking(TrackingRequest request) {
        log.info("Creating tracking for orderId={}", request.getOrderId());
        OrderTracking tracking = OrderTracking.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .status(request.getStatus() != null ? request.getStatus() : TrackingStatus.ORDER_CREATED)
                .statusMessage(request.getStatusMessage() != null ? request.getStatusMessage() : "Order received successfully")
                .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
                .currentLocation(request.getCurrentLocation())
                .carrierName(request.getCarrierName())
                .trackingNumber(request.getTrackingNumber())
                .build();
        tracking = trackingRepo.save(tracking);
        recordHistory(tracking, "SYSTEM");
        eventProducer.publishStatusUpdate(tracking.getOrderId(), tracking.getStatus().name(), tracking.getStatusMessage());
        return toResponse(tracking);
    }

    public TrackingResponse getTracking(String orderId) {
        return trackingRepo.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Tracking not found for orderId: " + orderId));
    }

    public List<TrackingHistoryResponse> getTrackingHistory(String orderId) {
        return historyRepo.findByOrderIdOrderByEventTimeDesc(orderId).stream()
                .map(this::toHistoryResponse).collect(Collectors.toList());
    }

    @Transactional
    public TrackingResponse updateStatus(String orderId, TrackingRequest request) {
        OrderTracking tracking = trackingRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Tracking not found: " + orderId));
        tracking.setStatus(request.getStatus());
        tracking.setStatusMessage(request.getStatusMessage());
        if (request.getCurrentLocation() != null) tracking.setCurrentLocation(request.getCurrentLocation());
        if (request.getEstimatedDeliveryTime() != null) tracking.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        if (request.getCarrierName() != null) tracking.setCarrierName(request.getCarrierName());
        if (request.getTrackingNumber() != null) tracking.setTrackingNumber(request.getTrackingNumber());
        tracking = trackingRepo.save(tracking);
        recordHistory(tracking, "AGENT");
        eventProducer.publishStatusUpdate(orderId, request.getStatus().name(), request.getStatusMessage());
        notificationService.sendStatusNotification(tracking);
        return toResponse(tracking);
    }

    // ── Kafka event handlers ─────────────────────────────────────────────────
    public void handleOrderCreated(String orderId, String payload) {
        try {
            if (trackingRepo.findByOrderId(orderId).isEmpty()) {
                OrderTracking t = OrderTracking.builder()
                        .orderId(orderId).customerId("unknown")
                        .status(TrackingStatus.ORDER_CREATED)
                        .statusMessage("Your order has been placed successfully!").build();
                trackingRepo.save(t);
                recordHistory(t, "KAFKA");
                eventProducer.publishStatusUpdate(orderId, "ORDER_CREATED", "Order placed");
            }
        } catch (Exception e) { log.error("Error handling order.created: {}", e.getMessage()); }
    }

    public void handleOrderProcessed(String orderId, String payload) {
        updateStatusFromKafka(orderId, TrackingStatus.PREPARING, "Your order is being prepared");
    }

    public void handlePaymentCompleted(String orderId, String payload) {
        updateStatusFromKafka(orderId, TrackingStatus.PAYMENT_CONFIRMED, "Payment confirmed! Your order is being prepared for dispatch");
    }

    public void handleOrderShipped(String orderId, String payload) {
        updateStatusFromKafka(orderId, TrackingStatus.DISPATCHED,
                "Your order has been dispatched! Estimated delivery: " +
                LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
    }

    public void handleOrderCancelled(String orderId, String payload) {
        updateStatusFromKafka(orderId, TrackingStatus.CANCELLED, "Your order has been cancelled. Refund will be processed in 3-5 business days");
    }

    private void updateStatusFromKafka(String orderId, TrackingStatus status, String message) {
        trackingRepo.findByOrderId(orderId).ifPresent(t -> {
            t.setStatus(status); t.setStatusMessage(message);
            trackingRepo.save(t); recordHistory(t, "KAFKA");
            eventProducer.publishStatusUpdate(orderId, status.name(), message);
        });
    }

    private void recordHistory(OrderTracking t, String updatedBy) {
        historyRepo.save(TrackingHistory.builder()
                .orderId(t.getOrderId()).status(t.getStatus())
                .statusMessage(t.getStatusMessage())
                .location(t.getCurrentLocation()).updatedBy(updatedBy).build());
    }

    private TrackingResponse toResponse(OrderTracking t) {
        return TrackingResponse.builder()
                .id(t.getId()).orderId(t.getOrderId()).customerId(t.getCustomerId())
                .status(t.getStatus()).statusMessage(t.getStatusMessage())
                .estimatedDeliveryTime(t.getEstimatedDeliveryTime())
                .currentLocation(t.getCurrentLocation()).carrierName(t.getCarrierName())
                .trackingNumber(t.getTrackingNumber())
                .createdAt(t.getCreatedAt()).updatedAt(t.getUpdatedAt()).build();
    }

    private TrackingHistoryResponse toHistoryResponse(TrackingHistory h) {
        return TrackingHistoryResponse.builder()
                .id(h.getId()).orderId(h.getOrderId()).status(h.getStatus())
                .statusMessage(h.getStatusMessage()).location(h.getLocation())
                .updatedBy(h.getUpdatedBy()).eventTime(h.getEventTime()).build();
    }
}
