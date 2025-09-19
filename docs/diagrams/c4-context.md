# C4 - Context Diagram

```mermaid
C4Context
    title 123Vendas - Sales Service (Context)

    Person(customer, "Client App / User", "Front-end, Postman, or external caller")

    System_Boundary(s1, "Sales Service") {
        System(sales_api, "Sales API", "Spring Boot REST API for Sales")
    }

    SystemDb(db, "Relational DB", "H2/PostgreSQL")
    System(prom, "Prometheus", "Scrapes metrics from Actuator")
    System(graf, "Grafana", "Dashboards for metrics")
    System(crm, "CRM/Stock Systems", "External systems providing product/customer/branch references")

    Rel(customer, sales_api, "CRUD Sales + Cancel", "HTTP/JSON")
    Rel(sales_api, db, "Persist sales & items", "JPA")
    Rel(sales_api, prom, "Expose metrics /actuator/prometheus", "HTTP/Prometheus")
    Rel(graf, prom, "Query metrics", "HTTP")
    Rel(sales_api, crm, "References (IDs/Names) [denormalized]", "" )
```
