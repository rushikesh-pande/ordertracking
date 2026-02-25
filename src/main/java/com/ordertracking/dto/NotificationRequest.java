package com.ordertracking.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationRequest {
    private String orderId;
    private String customerId;
    private String channel;   // EMAIL, SMS, PUSH
    private String message;
    private String email;
    private String phone;
}
