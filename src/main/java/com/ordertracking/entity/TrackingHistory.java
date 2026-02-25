package com.ordertracking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_history")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TrackingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    private TrackingStatus status;

    private String statusMessage;
    private String location;
    private String updatedBy;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @PrePersist
    protected void onCreate() {
        eventTime = LocalDateTime.now();
    }
}
