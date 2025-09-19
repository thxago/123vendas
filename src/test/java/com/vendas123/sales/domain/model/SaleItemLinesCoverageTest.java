package com.vendas123.sales.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaleItemLinesCoverageTest {

    @Test
    void recalculate_calls_pricing_and_sets_discount_and_total() {
        SaleItem it = new SaleItem("P1","Prod", 10, new BigDecimal("2.00"));
        // qty 10 => 20% desconto -> gross=20.00, discount=4.00, total=16.00
        assertEquals(new BigDecimal("4.00"), it.getDiscount());
        assertEquals(new BigDecimal("16.00"), it.getTotal());
    }
}
