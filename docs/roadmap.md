# Roadmap

## Next Backend Enhancements

- Add Flyway migrations instead of Hibernate `ddl-auto`.
- Add Testcontainers with PostgreSQL for concurrency tests.
- Add a payment attempt table.
- Add cancellation and refund saga.
- Add transactional outbox and async workers.
- Add OpenAPI documentation.
- Add authentication and rate limiting.
- Add structured JSON logging and trace IDs.

## Next Frontend Enhancements

- Add booking details page.
- Add conflict/load-test panel for optimistic locking demo.
- Add circuit breaker status display from actuator.
- Add admin controls for inventory restocking.

## Production Cloud Sketch

- AWS ALB or API Gateway in front of booking API.
- ECS/Fargate or Kubernetes for Spring Boot services.
- RDS PostgreSQL for transactional data.
- ElastiCache Redis for cache, locks where appropriate, and rate limiting.
- MSK/Kafka or EventBridge/SQS for events and async jobs.
- OpenSearch for search.
- S3 plus Athena/Glue or Snowflake/BigQuery for analytics.
- Prometheus/Grafana or Datadog for metrics and alerts.
