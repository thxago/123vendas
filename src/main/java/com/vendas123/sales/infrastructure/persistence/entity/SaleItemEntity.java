package com.vendas123.sales.infrastructure.persistence.entity;

import com.vendas123.sales.domain.model.SaleItem;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
public class SaleItemEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sale_id", nullable = false)
	private SaleEntity sale;

	@Column(nullable = false)
	private String productExternalId;

	@Column(nullable = false)
	private String productName;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal unitPrice;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal discount;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal total;

	public static SaleItemEntity fromDomain(SaleItem item) {
		SaleItemEntity e = new SaleItemEntity();
		e.productExternalId = item.getProductExternalId();
		e.productName = item.getProductName();
		e.quantity = item.getQuantity();
		e.unitPrice = item.getUnitPrice();
		e.discount = item.getDiscount();
		e.total = item.getTotal();
		return e;
	}

	public SaleItem toDomain() {
		return new SaleItem(productExternalId, productName, quantity, unitPrice);
	}

	public void setSale(SaleEntity sale) { this.sale = sale; }
}
