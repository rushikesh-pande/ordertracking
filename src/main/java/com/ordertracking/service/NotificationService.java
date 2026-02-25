package com.ordertracking.service;

import com.ordertracking.entity.OrderTracking;
import com.ordertracking.kafka.TrackingEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TrackingEventProducer eventProducer;

    public void sendStatusNotification(OrderTracking tracking) {
        String message = buildNotificationMessage(tracking);
        // Publish to Kafka — downstream notification services consume these
        eventProducer.publishEmailNotification(
                tracking.getOrderId(), "customer@example.com", message);
        eventProducer.publishSmsNotification(
                tracking.getOrderId(), "+91-9999999999", message);
        log.info("Notification dispatched for orderId={} status={}", tracking.getOrderId(), tracking.getStatus());
    }

    private String buildNotificationMessage(OrderTracking t) {
        return switch (t.getStatus()) {
            case ORDER_CREATED       -> "Your order " + t.getOrderId() + " has been placed successfully!";
            case PAYMENT_CONFIRMED   -> "Payment confirmed for order " + t.getOrderId() + ". We are preparing your order.";
            case DISPATCHED          -> "Your order " + t.getOrderId() + " has been dispatched! ETA: " + t.getEstimatedDeliveryTime();
            case OUT_FOR_DELIVERY    -> "Your order " + t.getOrderId() + " is out for delivery. Expect it today!";
            case DELIVERED           -> "Your order " + t.getOrderId() + " has been delivered. Enjoy!";
            case CANCELLED           -> "Order " + t.getOrderId() + " cancelled. Refund in 3-5 business days.";
            default                  -> "Order " + t.getOrderId() + " status: " + t.getStatus().name();
        };
    }
}
