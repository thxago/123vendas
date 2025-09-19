package com.vendas123.sales.domain.model;

import com.vendas123.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SaleItemTest {

    @Test
    void recalculate_noDiscount_under4() {
        SaleItem it = new SaleItem("P1", "Prod 1", 3, new BigDecimal("10.00"));
        assertEquals(new BigDecimal("0.00"), it.getDiscount());
        assertEquals(new BigDecimal("30.00"), it.getTotal());
    }

    @Test
    void recalculate_10percent_for4to9() {
        SaleItem it = new SaleItem("P1", "Prod 1", 5, new BigDecimal("10.00"));
        assertEquals(new BigDecimal("5.00"), it.getDiscount());
        assertEquals(new BigDecimal("45.00"), it.getTotal());
    }

    @Test
    void recalculate_20percent_for10to20() {
        SaleItem it = new SaleItem("P1", "Prod 1", 10, new BigDecimal("10.00"));
        assertEquals(new BigDecimal("20.00"), it.getDiscount());
        assertEquals(new BigDecimal("80.00"), it.getTotal());
    }

    @Test
    void quantity_mustBePositive_andMax20_onCreate() {
        assertThrows(BusinessException.class, () -> new SaleItem("P1","Prod 1", 0, new BigDecimal("1")));
        assertThrows(BusinessException.class, () -> new SaleItem("P1","Prod 1", 21, new BigDecimal("1")));
    }

    @Test
    void quantity_mustBePositive_andMax20_onSet() {
        SaleItem it = new SaleItem("P1", "Prod 1", 1, new BigDecimal("10.00"));
        assertThrows(BusinessException.class, () -> it.setQuantity(0));
        assertThrows(BusinessException.class, () -> it.setQuantity(21));
    }
}
