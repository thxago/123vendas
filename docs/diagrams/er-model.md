# ER Model - Sales Persistence

```mermaid
erDiagram
    SALE ||--o{ SALE_ITEM : contains
    SALE {
        UUID id PK
        VARCHAR sale_number
        TIMESTAMP created_at
        DECIMAL total_amount
        VARCHAR status
    }
    SALE_ITEM {
        BIGINT id PK
        UUID sale_id FK
        VARCHAR product_code
        VARCHAR description
        INT quantity
        DECIMAL unit_price
        DECIMAL discount
        DECIMAL total_price
    }
```
