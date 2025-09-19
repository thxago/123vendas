package com.vendas123.sales.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SaleRestoreTest {

    @Test
    void restore_builds_sale_without_side_effects() {
        UUID id = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();
        SaleItem item = new SaleItem("P1", "Prod 1", 4, new BigDecimal("10.00"));
        Sale s = Sale.restore(id, "S-1", date, "C1","Client","B1","Branch",
                List.of(item), SaleStatus.ACTIVE, new BigDecimal("36.00"));
        assertEquals(id, s.getId());
        assertEquals("S-1", s.getSaleNumber());
        assertEquals(date, s.getSaleDate());
        assertEquals(1, s.getItems().size());
        assertEquals(new BigDecimal("36.00"), s.getTotalAmount());
        assertEquals(SaleStatus.ACTIVE, s.getStatus());
    }
}
