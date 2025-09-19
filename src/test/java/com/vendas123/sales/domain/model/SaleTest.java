package com.vendas123.sales.domain.model;

import com.vendas123.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    void replaceItems_mustHaveAtLeastOne_null() {
        Sale sale = Sale.create("S-1", LocalDateTime.now(),
                "C1", "Client 1", "B1", "Branch 1",
                List.of(item(1, "10.00")));
        assertThrows(BusinessException.class, () -> sale.replaceItems(null));
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

    @Test
    void cancel_is_idempotent_when_already_cancelled() {
        Sale sale = Sale.create("S-1", LocalDateTime.now(),
                "C1", "Client 1", "B1", "Branch 1",
                List.of(item(1, "10.00")));
        sale.cancel();
        assertEquals(SaleStatus.CANCELLED, sale.getStatus());
        // segunda chamada não altera nem lança
        sale.cancel();
        assertEquals(SaleStatus.CANCELLED, sale.getStatus());
}

    @Test
    void create_mustHaveAtLeastOneItem_null() {
        assertThrows(BusinessException.class, () ->
                Sale.create("S-1", LocalDateTime.now(),
                        "C1", "Client 1", "B1", "Branch 1",
                        null)
        );
    }

    @Test
    void create_mustHaveAtLeastOneItem_empty() {
        assertThrows(BusinessException.class, () ->
                Sale.create("S-1", LocalDateTime.now(),
                        "C1", "Client 1", "B1", "Branch 1",
                        List.of())
        );
    }

    @Test
    void restore_mustHaveAtLeastOneItem_null() {
        assertThrows(BusinessException.class, () ->
                Sale.restore(UUID.randomUUID(), "S-1", LocalDateTime.now(),
                        "C1", "Client", "B1", "Branch",
                        null, // items null
                        SaleStatus.ACTIVE,
                        new BigDecimal("0.00"))
        );
    }

    @Test
    void restore_mustHaveAtLeastOneItem_empty() {
        assertThrows(BusinessException.class, () ->
                Sale.restore(UUID.randomUUID(), "S-1", LocalDateTime.now(),
                        "C1", "Client", "B1", "Branch",
                        List.of(), // items empty
                        SaleStatus.ACTIVE,
                        new BigDecimal("0.00"))
        );
    }

}
