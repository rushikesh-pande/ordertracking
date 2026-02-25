package com.ordertracking.dto;

import com.ordertracking.entity.TrackingStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TrackingHistoryResponse {
    private Long id;
    private String orderId;
    private TrackingStatus status;
    private String statusMessage;
    private String location;
    private String updatedBy;
    private LocalDateTime eventTime;
}
