# 123Vendas - Sales Service (Java 17 / Spring Boot 3)

Sales API implementing DDD and business rules:
- External Identities for CRM/Stock references (ids + denormalized names kept in Sale).
- Discount policy per item quantity:
  - < 4: 0%
  - 4–9: 10%
  - 10–20: 20%
  - > 20: not allowed
- JSON logs, Observability with Micrometer + Prometheus/Grafana.
- 100% domain logic covered by automated tests.

## Tech
- Java 17, Spring Boot 3 (Web, Validation, Data JPA, Actuator)
- H2 (dev/test)
- Micrometer Prometheus
- Logback JSON
- JUnit 5

## How to run (local)
  mvn spring-boot:run
  http://localhost:8080/api/v1/sales
  http://localhost:8080/actuator/health
  http://localhost:8080/actuator/prometheus
## Running Tests and Observability Stack with Docker Compose
1) Build and run locally with Maven
  - Use your IDE or `mvn spring-boot:run` to start the service on port 8080.
  - Healthcheck: GET http://localhost:8080/actuator/health

2) Run tests
  - Unit + integration tests: `mvn test`

3) Docker image
  - Build: Dockerfile uses multi-stage Maven build.
  - Run: `docker compose up --build` (includes Prometheus/Grafana stack below).

  mvn clean verify

## Observability (Docker)
- Build app image and start stack (app + Prometheus + Grafana):
The application exposes Micrometer metrics at `/actuator/prometheus`.

Docker Compose stack included:
- Prometheus on http://localhost:9090 (scrapes the app)
- Grafana on http://localhost:3000 (admin / admin)

How to run:
1) docker compose up --build
2) Open Grafana, add Prometheus data source at http://prometheus:9090
3) Import dashboard 12900 (Spring Boot Micrometer) or build your own panels.
  docker compose -f docker/docker-compose.yml up --build
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (user: admin / pass: admin)
- Add Prometheus data source: http://prometheus:9090
- Import a JVM/ Micrometer dashboard (e.g., 4701 or 6756) or create custom graphs:
  - rate(http_server_requests_seconds_count[1m])
  - http_server_requests_seconds_max
  - jvm_threads_live_threads
  - process_cpu_usage

## JSON logs
- Configured via Logback with logstash encoder (logback-spring.xml).
- Includes traceId/spanId when Sleuth-like tracing is present (compatible JSON fields).

## API (CRUD + cancel)
- POST /api/v1/sales
- GET /api/v1/sales/{id}
- GET /api/v1/sales?page=0&size=20
- PUT /api/v1/sales/{id}
- POST /api/v1/sales/{id}/cancel
- DELETE /api/v1/sales/{id} (only when CANCELLED)

See controller and DTOs for request/response contracts.

## Architecture
- Domain (aggregate): Sale (root), SaleItem (child), SaleStatus (enum)
- Domain service: PricingPolicy (discount rules)
- Application service: SaleService (use cases, transactions)
- Infrastructure: Spring Data JPA repository
- API: REST Controller, DTOs, Exception Handler

External Identities:
- Sale stores customerId/customerName and branchId/branchName.
- Product references: productId/productName on each SaleItem.

## Git Flow + Conventional Commits
- Branching:
  - main: stable releases
  - develop: integration branch
  - feature/*: new features from develop
  - hotfix/*: fixes from main
  - release/*: release prep from develop
- Merge strategy: PRs with review, squash or merge commits allowed depending on repo policy.
- Conventional Commits:
  - feat: new feature
  - fix: bug fix
  - docs: documentation
  - test: tests
  - refactor: code change without feature/bug
  - perf: performance
  - build/chore/ci: tooling
- Examples:
  - feat(sales): add cancel sale endpoint
  - fix(domain): prevent >20 items per product
  - test(policy): cover 10–20 tier discount

## Notes
- 100% domain logic covered by unit tests (PricingPolicy and Sale aggregate).
- Integration tests for the REST API using MockMvc + H2.
