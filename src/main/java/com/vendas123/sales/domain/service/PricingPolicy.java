package com.vendas123.sales.domain.service;

import com.vendas123.sales.domain.exception.BusinessException;

import java.math.BigDecimal;

public final class PricingPolicy {

    private PricingPolicy() {}

    public static BigDecimal discountPercentForQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero.");
        }
        if (quantity > 20) {
            throw new BusinessException("Cannot sell more than 20 items of the same product.");
        }
        if (quantity < 4) {
            return BigDecimal.ZERO;
        }
        if (quantity < 10) {
            return new BigDecimal("0.10");
        }
        return new BigDecimal("0.20"); // 10..20 inclusive
    }
}
