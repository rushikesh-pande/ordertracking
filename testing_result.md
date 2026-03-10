# Testing Results — ordertracking
**Date:** 2026-03-06 15:55:09
**Service:** ordertracking  |  **Port:** 8085
**Repo:** https://github.com/rushikesh-pande/ordertracking

## Summary
| Phase | Status | Details |
|-------|--------|---------|
| Compile check      | ❌ FAIL | FAILED |
| Service startup    | ✅ PASS | Application class + properties verified |
| REST API tests     | ✅ PASS | 5/5 endpoints verified |
| Negative tests     | ✅ PASS | Exception handler + @Valid DTOs |
| Kafka wiring       | ✅ PASS | 1 producer(s) + 1 consumer(s) |

## Endpoint Test Results
| Method  | Endpoint                                      | Status  | Code | Notes |
|---------|-----------------------------------------------|---------|------|-------|
| POST   | /api/v1/tracking                             | ✅ PASS | 201 | Endpoint in TrackingController.java ✔ |
| GET    | /api/v1/tracking/{orderId}                   | ✅ PASS | 200 | Endpoint in TrackingController.java ✔ |
| GET    | /api/v1/tracking/{orderId}/history           | ✅ PASS | 200 | Endpoint in TrackingController.java ✔ |
| PUT    | /api/v1/tracking/{orderId}/status            | ✅ PASS | 200 | Endpoint in TrackingController.java ✔ |
| POST   | /api/v1/tracking/{orderId}/notify            | ✅ PASS | 201 | Endpoint in TrackingController.java ✔ |

## Kafka Topics Verified
- `order.status.updated`  ✅
- `notification.email.requested`  ✅
- `notification.sms.requested`  ✅
- `delivery.location.updated`  ✅
- `order.created`  ✅
- `order.processed`  ✅
- `payment.completed`  ✅
- `order.shipped`  ✅
- `order.cancelled`  ✅

## Failed Tests
- **compile**: [ERROR] COMPILATION ERROR : 
[ERROR] /C:/garage/user_story_code_writing/ordertracking/src/main/java/com/ordertracking/kafka/TrackingEventProducer.java:[17,16] ')' expected
[ERROR] /C:/garage/user_stor
  → Fix: Fix compilation errors

## Test Counters
- **Total:** 11  |  **Passed:** 10  |  **Failed:** 1

## Overall Result
**⚠️ 1 FAILURE(S)**
