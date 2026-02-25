package com.ordertracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String orderId;

    @NotBlank
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingStatus status;

    private String statusMessage;
    private String estimatedDeliveryTime;
    private String currentLocation;
    private String carrierName;
    private String trackingNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
