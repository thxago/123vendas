package com.vendas123.sales.domain.model;

import com.vendas123.shared.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class SaleItem {
	private String productExternalId;
	private String productName; // denormalized
	private int quantity;
	private BigDecimal unitPrice;
	private BigDecimal discount; // absolute value
	private BigDecimal total;    // unitPrice*qty - discount

	public SaleItem(String productExternalId, String productName, int quantity, BigDecimal unitPrice) {
		this.productExternalId = Objects.requireNonNull(productExternalId);
		this.productName = Objects.requireNonNull(productName);
		if (quantity < 1) throw new BusinessException("Quantity must be at least 1");
		if (quantity > 20) throw new BusinessException("Cannot sell more than 20 items of the same product");
		if (unitPrice == null || unitPrice.signum() <= 0) throw new BusinessException("Unit price must be positive");
		this.quantity = quantity;
		this.unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
		recalculate();
	}

	public void recalculate() {
		var gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
		BigDecimal rate = BigDecimal.ZERO;
		if (quantity >= 10) {
			rate = new BigDecimal("0.20");
		} else if (quantity >= 4) {
			rate = new BigDecimal("0.10");
		}
		this.discount = gross.multiply(rate).setScale(2, RoundingMode.HALF_UP);
		this.total = gross.subtract(discount).setScale(2, RoundingMode.HALF_UP);
	}

	public String getProductExternalId() { return productExternalId; }
	public String getProductName() { return productName; }
	public int getQuantity() { return quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public BigDecimal getDiscount() { return discount; }
	public BigDecimal getTotal() { return total; }

	public void setQuantity(int quantity) {
		if (quantity < 1) throw new BusinessException("Quantity must be at least 1");
		if (quantity > 20) throw new BusinessException("Cannot sell more than 20 items of the same product");
		this.quantity = quantity;
		recalculate();
	}
}
