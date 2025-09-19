package com.vendas123.sales.domain.model;

import com.vendas123.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SaleItemExtraTest {

    @Test
    void unitPrice_must_be_positive_and_non_null() {
        assertThrows(BusinessException.class, () -> new SaleItem("P","N", 1, null));
        assertThrows(BusinessException.class, () -> new SaleItem("P","N", 1, new BigDecimal("0")));
        assertThrows(BusinessException.class, () -> new SaleItem("P","N", 1, new BigDecimal("-1")));
    }

    @Test
    void unitPrice_is_rounded_to_two_decimals() {
        SaleItem it = new SaleItem("P","N", 1, new BigDecimal("10.005"));
        assertEquals(new BigDecimal("10.01"), it.getUnitPrice());
    }
}
