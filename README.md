# 123Vendas - Sales Service (Java 17 / Spring Boot 3)

Sales API implementing DDD and business rules:
- External identities for CRM/Stock references (ids + denormalized names kept in Sale).
- Discount policy per item quantity: <4: 0%, 4–9: 10%, 10–20: 20% (above 20 not allowed).
- JSON logs, Observability with Micrometer + Prometheus/Grafana.
- Automated tests for domain, service and API.

## Tech
- Java 17, Spring Boot 3 (Web, Validation, Data JPA, Actuator)
- H2 (dev/test)
- Micrometer Prometheus
- Logback JSON
- JUnit 5 + Mockito

## Run locally
- Start: mvn spring-boot:run
- API: http://localhost:8080/api/v1/sales
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus

## Tests
- Run all tests: mvn test

## Docker Compose (App + Prometheus + Grafana)
- Up (build images): docker compose up --build
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (user: admin / pass: admin)
- Add Prometheus datasource in Grafana: http://prometheus:9090
- Import dashboard 12900 (Spring Boot Micrometer) or create custom panels.

The application exposes Micrometer metrics at /actuator/prometheus.

## API (CRUD + cancel)
- POST /api/v1/sales
- GET /api/v1/sales/{id}
- GET /api/v1/sales?page=0&size=20
- PUT /api/v1/sales/{id}
- PATCH /api/v1/sales/{id}/cancel
- DELETE /api/v1/sales/{id} (only when CANCELLED)

See controller and DTOs for request/response contracts.

## Architecture
- Domain (aggregate): Sale (root), SaleItem (child), SaleStatus (enum)
- Domain service: PricingPolicy (discount rules)
- Application service: SaleService (use cases, transactions)
- Infrastructure: Spring Data JPA repository
- API: REST Controller, DTOs, Exception Handler

## Git Flow + Conventional Commits (suggested)
- Branches: main, develop, feature/*, hotfix/*, release/*
- Conventional commits: feat/fix/docs/test/refactor/perf/build/chore/ci

## Notes
- Domain rules covered by unit tests; REST API covered by integration tests with MockMvc + H2.
