package com.ordertracking.controller;

import com.ordertracking.dto.*;
import com.ordertracking.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
@Slf4j
public class TrackingController {

    private final TrackingService trackingService;

    /** POST /api/v1/tracking — Create initial tracking record */
    @PostMapping
    public ResponseEntity<TrackingResponse> create(@Valid @RequestBody TrackingRequest req) {
        log.info("POST /api/v1/tracking orderId={}", req.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(trackingService.createTracking(req));
    }

    /** GET /api/v1/tracking/{orderId} — Get current tracking status */
    @GetMapping("/{orderId}")
    public ResponseEntity<TrackingResponse> getStatus(@PathVariable String orderId) {
        log.info("GET /api/v1/tracking/{}", orderId);
        return ResponseEntity.ok(trackingService.getTracking(orderId));
    }

    /** GET /api/v1/tracking/{orderId}/history — Get full tracking history */
    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<TrackingHistoryResponse>> getHistory(@PathVariable String orderId) {
        log.info("GET /api/v1/tracking/{}/history", orderId);
        return ResponseEntity.ok(trackingService.getTrackingHistory(orderId));
    }

    /** PUT /api/v1/tracking/{orderId}/status — Update tracking status */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<TrackingResponse> updateStatus(
            @PathVariable String orderId,
            @Valid @RequestBody TrackingRequest req) {
        log.info("PUT /api/v1/tracking/{}/status status={}", orderId, req.getStatus());
        return ResponseEntity.ok(trackingService.updateStatus(orderId, req));
    }

    /** POST /api/v1/tracking/{orderId}/notify — Manually trigger notification */
    @PostMapping("/{orderId}/notify")
    public ResponseEntity<String> notify(@PathVariable String orderId) {
        log.info("POST /api/v1/tracking/{}/notify", orderId);
        TrackingResponse tr = trackingService.getTracking(orderId);
        return ResponseEntity.ok("Notification triggered for order " + orderId + " status=" + tr.getStatus());
    }
}
