# Sequence Diagram - Create Sale

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant API as SaleController
    participant S as SaleService
    participant D as Domain (Sale, SaleItem, PricingPolicy)
    participant R as SaleRepository (port)
    participant A as RepositoryAdapter
    participant J as SaleJpaRepository
    participant E as EventPublisher

    C->>API: POST /api/v1/sales (SaleRequest)
    API->>S: createSale(request)
    S->>D: new Sale(...); add items; recalculate
    D-->>S: Sale with totals
    S->>R: save(sale)
    R->>A: save(sale)
    A->>J: save(entity)
    J-->>A: entity persisted (id)
    A-->>R: sale restored
    R-->>S: sale with id
    S->>E: publish CompraEfetuada
    E-->>S: ack
    S-->>API: SaleResponse (201 Created + Location)
    API-->>C: 201 Created
```
