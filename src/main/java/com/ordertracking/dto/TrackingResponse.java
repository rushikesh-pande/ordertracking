package com.ordertracking.dto;

import com.ordertracking.entity.TrackingStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TrackingResponse {
    private Long id;
    private String orderId;
    private String customerId;
    private TrackingStatus status;
    private String statusMessage;
    private String estimatedDeliveryTime;
    private String currentLocation;
    private String carrierName;
    private String trackingNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
