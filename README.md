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
