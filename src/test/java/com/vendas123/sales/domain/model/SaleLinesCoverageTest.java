package com.vendas123.sales.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SaleLinesCoverageTest {

    private SaleItem item(String id, int qty, String price) {
        return new SaleItem(id, "Prod " + id, qty, new BigDecimal(price));
    }

    @Test
    void create_uses_now_when_saleDate_null() {
        Sale s = Sale.create("S-xyz", null, "C1","Client","B1","Branch", List.of(item("P1", 1, "10.00")));
        assertNotNull(s.getSaleDate());
    }

    @Test
    void restore_defaults_status_and_scales_totalAmount() {
        var items = List.of(item("P1", 2, "10.00"));
        // totalAmount sem escala (45) será normalizado para 45.00
        BigDecimal persistedTotal = new BigDecimal("45");
        Sale restored = Sale.restore(UUID.randomUUID(), "S-1", LocalDateTime.now(),
                "C1","Client","B1","Branch", items,
                null, // status nulo -> ACTIVE
                persistedTotal);
        assertEquals(SaleStatus.ACTIVE, restored.getStatus());
        assertEquals(new BigDecimal("45.00"), restored.getTotalAmount());
    }

    @Test
    void recalculateTotals_updates_sum_after_item_change() {
        SaleItem i = item("P1", 3, "10.00"); // sem desconto (3 * 10 = 30)
        Sale s = Sale.create("S-1", LocalDateTime.now(), "C","N","B","BN", List.of(i));
        assertEquals(new BigDecimal("30.00"), s.getTotalAmount());
        i.setQuantity(5); // 5 * 10 com 10% desc => 45.00
        s.recalculateTotals();
        assertEquals(new BigDecimal("45.00"), s.getTotalAmount());
    }
}
