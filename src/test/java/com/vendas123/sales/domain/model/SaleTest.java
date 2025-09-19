package com.vendas123.sales.domain.model;

import com.vendas123.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleTest {

    private static SaleItem item(int qty, String price) {
        return new SaleItem("P1", "Prod 1", qty, new BigDecimal(price));
    }

    @Test
    void create_and_recalculateTotals() {
        Sale sale = Sale.create("S-1", LocalDateTime.now(),
                "C1", "Client 1", "B1", "Branch 1",
                List.of(item(2, "10.00"), item(4, "5.00")));
        // 2*10 + 4*5 = 20 + 20 = 40 ; second has 10% discount => 2.00 off
        assertEquals(new BigDecimal("38.00"), sale.getTotalAmount());
    }

    @Test
    void replaceItems_mustHaveAtLeastOne() {
        Sale sale = Sale.create("S-1", LocalDateTime.now(),
                "C1", "Client 1", "B1", "Branch 1",
                List.of(item(1, "10.00")));
        assertThrows(BusinessException.class, () -> sale.replaceItems(List.of()));
    }

    @Test
    void cancel_changesStatus() {
        Sale sale = Sale.create("S-1", LocalDateTime.now(),
                "C1", "Client 1", "B1", "Branch 1",
                List.of(item(1, "10.00")));
        assertEquals(SaleStatus.ACTIVE, sale.getStatus());
        sale.cancel();
        assertEquals(SaleStatus.CANCELLED, sale.getStatus());
    }
}
