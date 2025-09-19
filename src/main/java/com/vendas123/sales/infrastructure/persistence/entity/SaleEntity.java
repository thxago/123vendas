package com.vendas123.sales.infrastructure.persistence.entity;

import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.model.SaleItem;
import com.vendas123.sales.domain.model.SaleStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "sales")
public class SaleEntity {
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(nullable = false, unique = true)
	private String saleNumber;

	@Column(nullable = false)
	private LocalDateTime saleDate;

	@Column(nullable = false)
	private String clientExternalId;
	@Column(nullable = false)
	private String clientName;

	@Column(nullable = false)
	private String branchExternalId;
	@Column(nullable = false)
	private String branchName;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SaleStatus status;

	@OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<SaleItemEntity> items = new ArrayList<>();

	public static SaleEntity fromDomain(Sale sale) {
		SaleEntity e = new SaleEntity();
		e.id = sale.getId();
		e.saleNumber = sale.getSaleNumber();
		e.saleDate = sale.getSaleDate();
		e.clientExternalId = sale.getClientExternalId();
		e.clientName = sale.getClientName();
		e.branchExternalId = sale.getBranchExternalId();
		e.branchName = sale.getBranchName();
		e.totalAmount = sale.getTotalAmount();
		e.status = sale.getStatus();
		e.items.clear();
		for (SaleItem it : sale.getItems()) {
			SaleItemEntity i = SaleItemEntity.fromDomain(it);
			i.setSale(e);
			e.items.add(i);
		}
		return e;
	}

	public Sale toDomain() {
	List<SaleItem> domainItems = items.stream()
		.map(SaleItemEntity::toDomain)
		.collect(Collectors.toList());
	return Sale.restore(
		this.id,
		this.saleNumber,
		this.saleDate,
		this.clientExternalId, this.clientName,
		this.branchExternalId, this.branchName,
		domainItems,
		this.status,
		this.totalAmount
	);
	}

	// getters/setters omitted for brevity
}
