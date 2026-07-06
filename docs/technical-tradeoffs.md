# Technical Tradeoffs

## Monolith First vs Microservices

This project keeps booking, inventory, idempotency, and payment orchestration in one Spring Boot service. That is intentional for a portfolio project because the reliability patterns are easier to inspect and run locally.

At scale, these domains would likely split into services:

- Booking Orchestrator
- Inventory Service
- Payment Service
- Supplier Connector Service
- Notification Service
- Analytics/Event Pipeline

The cost of splitting early is distributed transactions, more deployment overhead, harder local development, and more observability requirements.

## Optimistic Locking vs Pessimistic Locking

Optimistic locking uses a version column and fails fast when two transactions update the same inventory row. It is simple, database-native, and performs well when contention is moderate.

Pessimistic locking can be useful for extremely scarce inventory, but it holds database locks longer and can reduce throughput. A mature travel system may use supplier reservation tokens instead of only local counters.

## Synchronous Saga vs Async Saga

The current flow is synchronous so the frontend can demonstrate success and compensation immediately. Production systems often move long supplier confirmations to async sagas with durable state machines.

Production upgrades:

- store each saga step and attempt
- publish events through a transactional outbox
- run compensations from a worker
- make every external call idempotent
- support manual repair for stuck sagas

## Storing Idempotency Responses

The current implementation stores the full JSON response for each idempotency key. That makes retries deterministic and simple.

Production concerns:

- apply TTL, usually 24 hours to 7 days depending on client behavior
- scope keys by authenticated user or partner
- reject key reuse with different request hash
- encrypt or avoid sensitive response fields
- add cleanup jobs and indexes by creation time

## Database Choice

PostgreSQL is the default production database because it provides transactions, row-level locking, strong indexing, JSON support, and operational maturity.

H2 is used for frictionless local demos only. It should not be treated as production-equivalent for concurrency behavior.

## Queue Choice

Kafka is a strong fit for domain events, analytics, and replay. SQS/RabbitMQ are simpler for task queues and retry workers. A production Expedia-style design may use both: Kafka for durable event streams and SQS-like queues for operational work.

## Frontend Choice

The frontend is intentionally small. It exists to exercise backend behavior, not to be a full travel product UI. A production frontend would add authentication, search filters, itinerary details, payment collection, cancellation flows, and support tooling.
