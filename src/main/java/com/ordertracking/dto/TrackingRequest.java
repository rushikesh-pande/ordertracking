package com.ordertracking.dto;

import com.ordertracking.entity.TrackingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TrackingRequest {
    @NotBlank private String orderId;
    @NotBlank private String customerId;
    @NotNull  private TrackingStatus status;
    private String statusMessage;
    private String estimatedDeliveryTime;
    private String currentLocation;
    private String carrierName;
    private String trackingNumber;
}
