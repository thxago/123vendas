# ER Model - Sales Persistence

```mermaid
erDiagram
    SALE ||--o{ SALE_ITEM : contains
    SALE {
        UUID id PK
        TIMESTAMP created_at
        DECIMAL(15,2) total_amount
        VARCHAR status
    }
    SALE_ITEM {
        UUID id PK
        UUID sale_id FK
        VARCHAR product_code
        VARCHAR description
        INT quantity
        DECIMAL(15,2) unit_price
        DECIMAL(15,2) total_price
    }
```
