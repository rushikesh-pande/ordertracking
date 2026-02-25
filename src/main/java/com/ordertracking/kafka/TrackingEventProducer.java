package com.ordertracking.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrackingEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishStatusUpdate(String orderId, String status, String message) {
        String payload = String.format(
            "{"orderId":"%s","status":"%s","message":"%s","timestamp":"%s"}",
            orderId, status, message, java.time.LocalDateTime.now());
        kafkaTemplate.send("order.status.updated", orderId, payload);
        log.info("Published order.status.updated for orderId={} status={}", orderId, status);
    }

    public void publishEmailNotification(String orderId, String email, String message) {
        String payload = String.format(
            "{"orderId":"%s","email":"%s","message":"%s"}", orderId, email, message);
        kafkaTemplate.send("notification.email.requested", orderId, payload);
        log.info("Published email notification for orderId={}", orderId);
    }

    public void publishSmsNotification(String orderId, String phone, String message) {
        String payload = String.format(
            "{"orderId":"%s","phone":"%s","message":"%s"}", orderId, phone, message);
        kafkaTemplate.send("notification.sms.requested", orderId, payload);
        log.info("Published SMS notification for orderId={}", orderId);
    }

    public void publishLocationUpdate(String orderId, String location) {
        String payload = String.format(
            "{"orderId":"%s","location":"%s","timestamp":"%s"}",
            orderId, location, java.time.LocalDateTime.now());
        kafkaTemplate.send("delivery.location.updated", orderId, payload);
        log.info("Published location update for orderId={}", orderId);
    }
}
