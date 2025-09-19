package com.vendas123.sales.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleExtraTest {

    private SaleItem item(int qty, String price) {
        return new SaleItem("P1", "Prod 1", qty, new BigDecimal(price));
    }

    @Test
    void setters_update_fields_and_recalculate() {
        Sale s = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch", List.of(item(1, "10.00")));
        s.setClient("C2","Client 2");
        s.setBranch("B2","Branch 2");
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        s.setSaleDate(newDate);
        assertEquals("C2", s.getClientExternalId());
        assertEquals("B2", s.getBranchExternalId());
        assertEquals(newDate, s.getSaleDate());
    }

    @Test
    void default_status_is_active_and_items_unmodifiable() {
        Sale s = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch", List.of(item(1, "10.00")));
        assertEquals(SaleStatus.ACTIVE, s.getStatus());
        assertThrows(UnsupportedOperationException.class, () -> s.getItems().add(item(1, "1.00")));
    }
}
