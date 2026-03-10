# ordertracking — Enhancement #1

## Overview
Real-time Order Tracking & Status Updates microservice.
Listens to Kafka events from all other services and maintains a unified tracking view.

## Features
- ✅ Real-time order status tracking (12 states)
- ✅ Full audit history per order
- ✅ SMS/Email notification dispatch via Kafka
- ✅ Delivery location tracking
- ✅ Kafka consumer for all service events
- ✅ REST API for status query and update

## Kafka Topics
| Topic | Type | Description |
|-------|------|-------------|
| `order.status.updated` | Produces | Status changed |
| `notification.email.requested` | Produces | Email trigger |
| `notification.sms.requested` | Produces | SMS trigger |
| `delivery.location.updated` | Produces | Location update |
| `order.created` | Consumes | From createorder |
| `order.processed` | Consumes | From orderprocessing |
| `payment.completed` | Consumes | From paymentprocessing |
| `order.shipped` | Consumes | From ordershipping |
| `order.cancelled` | Consumes | From orderprocessing |

## API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST | /api/v1/tracking | Create tracking record |
| GET  | /api/v1/tracking/{orderId} | Get current status |
| GET  | /api/v1/tracking/{orderId}/history | Get full history |
| PUT  | /api/v1/tracking/{orderId}/status | Update status |
| POST | /api/v1/tracking/{orderId}/notify | Send notification |

## Running
```bash
mvn spring-boot:run
```
Port: **8085**

## Status Flow
ORDER_CREATED → ORDER_CONFIRMED → PAYMENT_PROCESSING → PAYMENT_CONFIRMED
→ PREPARING → DISPATCHED → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED

## 🔒 Security Enhancements

This service implements all 7 security enhancements:

| # | Enhancement | Implementation |
|---|-------------|----------------|
| 1 | **OAuth 2.0 / JWT** | `SecurityConfig.java` — stateless JWT auth, Bearer token validation |
| 2 | **API Rate Limiting** | `RateLimitingFilter.java` — 100 req/min per IP using Bucket4j |
| 3 | **Input Validation** | `InputSanitizer.java` — SQL injection, XSS, command injection prevention |
| 4 | **Data Encryption** | `EncryptionService.java` — AES-256-GCM for sensitive data at rest |
| 5 | **PCI DSS** | `PciDssAuditAspect.java` — Full audit trail for payment operations |
| 6 | **GDPR Compliance** | `GdprDataService.java` — Right to erasure, consent management, data export |
| 7 | **Audit Logging** | `AuditLogService.java` — All transactions logged with user, IP, timestamp |

### Security Endpoints
- `GET /api/v1/audit/recent?limit=100` — Recent audit events (ADMIN only)
- `GET /api/v1/audit/user/{userId}` — User's audit trail (ADMIN or self)
- `GET /api/v1/audit/violations` — Security violations (ADMIN only)

### JWT Authentication
```bash
# Include Bearer token in all requests:
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8085/api/v1/...
```

### Security Headers Added
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`
- `Referrer-Policy: strict-origin-when-cross-origin`
- `X-RateLimit-Remaining: <n>` (on every response)
