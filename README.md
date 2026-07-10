# Resilient Booking / Reservation Service

A Spring Boot API and small test console for an Expedia-style booking backend. The project deliberately demonstrates production concerns that matter in travel commerce:

- idempotent booking requests so client retries do not double-book
- optimistic locking on inventory rows so concurrent requests cannot oversell the last seat or room
- Resilience4j retries and circuit breaker around a simulated flaky payment gateway
- saga-style compensation for multi-step bookings: reserve inventory, charge payment, confirm booking, or release inventory when payment fails
- actuator metrics, Prometheus-ready endpoint, and docs that explain how this would scale

## Quick Start

Backend with in-memory H2:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Open `http://127.0.0.1:5173`. The API runs at `http://localhost:8080`.

Production-like local stack with Postgres:

```bash
docker compose up api postgres
```

Optional metrics:

```bash
docker compose --profile observability up prometheus
```

## Demo Flow

1. Start the API and frontend.
2. Pick an inventory item and create a booking.
3. Press `Retry Same Request`. The same `Idempotency-Key` returns the exact same response and inventory is not decremented again.
4. Check `Force payment failure` and create a new booking. The API reserves inventory, payment fails, and the saga compensates by releasing the inventory.
5. Create concurrent requests for an item with only a few units left. JPA `@Version` triggers optimistic-lock conflict instead of overselling.

## API

### `GET /api/inventory`

Returns seeded flights and hotel rooms with current inventory and version.

### `POST /api/bookings`

Requires `Idempotency-Key`.

```json
{
  "inventoryItemId": 1,
  "customerEmail": "traveler@example.com",
  "quantity": 1,
  "forcePaymentFailure": false
}
```

### `GET /api/bookings`

Returns recent bookings and their final saga status.

## Project Layout

```text
backend/   Spring Boot API
frontend/  Browser-based booking console
docs/      Architecture, scaling, and tradeoff write-ups
ops/       Prometheus config
```



Travel booking systems are not CRUD-only. They coordinate scarce inventory, payment side effects, retries from mobile/web clients, vendor outages, and reconciliation. This project focuses on those failure modes and documents the system design decisions behind them.

See [docs/system-design.md](docs/system-design.md) and [docs/technical-tradeoffs.md](docs/technical-tradeoffs.md).
