package com.ordertracking.kafka;

import com.ordertracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final TrackingService trackingService;

    @KafkaListener(topics = "order.created", groupId = "ordertracking-group")
    public void onOrderCreated(ConsumerRecord<String, String> record) {
        log.info("Received order.created event: key={}", record.key());
        trackingService.handleOrderCreated(record.key(), record.value());
    }

    @KafkaListener(topics = "order.processed", groupId = "ordertracking-group")
    public void onOrderProcessed(ConsumerRecord<String, String> record) {
        log.info("Received order.processed event: key={}", record.key());
        trackingService.handleOrderProcessed(record.key(), record.value());
    }

    @KafkaListener(topics = "payment.completed", groupId = "ordertracking-group")
    public void onPaymentCompleted(ConsumerRecord<String, String> record) {
        log.info("Received payment.completed event: key={}", record.key());
        trackingService.handlePaymentCompleted(record.key(), record.value());
    }

    @KafkaListener(topics = "order.shipped", groupId = "ordertracking-group")
    public void onOrderShipped(ConsumerRecord<String, String> record) {
        log.info("Received order.shipped event: key={}", record.key());
        trackingService.handleOrderShipped(record.key(), record.value());
    }

    @KafkaListener(topics = "order.cancelled", groupId = "ordertracking-group")
    public void onOrderCancelled(ConsumerRecord<String, String> record) {
        log.info("Received order.cancelled event: key={}", record.key());
        trackingService.handleOrderCancelled(record.key(), record.value());
    }
}
