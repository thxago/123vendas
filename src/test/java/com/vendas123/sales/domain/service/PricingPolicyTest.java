package com.vendas123.sales.domain.service;

import com.vendas123.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingPolicyTest {

    @Test
    void discount_0_for_1to3() {
        assertEquals(BigDecimal.ZERO, PricingPolicy.discountPercentForQuantity(1));
        assertEquals(BigDecimal.ZERO, PricingPolicy.discountPercentForQuantity(3));
    }

    @Test
    void discount_10_for_4to9() {
        assertEquals(new BigDecimal("0.10"), PricingPolicy.discountPercentForQuantity(4));
        assertEquals(new BigDecimal("0.10"), PricingPolicy.discountPercentForQuantity(9));
    }

    @Test
    void discount_20_for_10to20() {
        assertEquals(new BigDecimal("0.20"), PricingPolicy.discountPercentForQuantity(10));
        assertEquals(new BigDecimal("0.20"), PricingPolicy.discountPercentForQuantity(20));
    }

    @Test
    void invalid_quantities_throw() {
        assertThrows(BusinessException.class, () -> PricingPolicy.discountPercentForQuantity(0));
        assertThrows(BusinessException.class, () -> PricingPolicy.discountPercentForQuantity(21));
    }
}
