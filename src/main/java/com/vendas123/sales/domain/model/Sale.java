package com.vendas123.sales.domain.model;

import com.vendas123.shared.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class Sale {
	private UUID id;
	private String saleNumber;
	private LocalDateTime saleDate;

	private String clientExternalId;
	private String clientName;

	private String branchExternalId;
	private String branchName;

	private List<SaleItem> items = new ArrayList<>();
	private BigDecimal totalAmount = BigDecimal.ZERO;

	private SaleStatus status = SaleStatus.ACTIVE;

	private Sale() {}

	public static Sale create(String saleNumber,
	                          LocalDateTime saleDate,
	                          String clientExternalId, String clientName,
	                          String branchExternalId, String branchName,
	                          List<SaleItem> items) {
		Sale s = new Sale();
		s.id = UUID.randomUUID();
		s.saleNumber = Objects.requireNonNull(saleNumber);
		s.saleDate = Objects.requireNonNullElseGet(saleDate, LocalDateTime::now);
		s.clientExternalId = Objects.requireNonNull(clientExternalId);
		s.clientName = Objects.requireNonNull(clientName);
		s.branchExternalId = Objects.requireNonNull(branchExternalId);
		s.branchName = Objects.requireNonNull(branchName);
		if (items == null || items.isEmpty()) throw new BusinessException("Sale must have at least one item");
		s.items.addAll(items);
		s.recalculateTotals();
		return s;
	}

	public void replaceItems(List<SaleItem> newItems) {
		if (newItems == null || newItems.isEmpty()) throw new BusinessException("Sale must have at least one item");
		this.items.clear();
		this.items.addAll(newItems);
		recalculateTotals();
	}

	public void cancel() {
		if (this.status == SaleStatus.CANCELLED) return;
		this.status = SaleStatus.CANCELLED;
	}

	public void recalculateTotals() {
		BigDecimal sum = BigDecimal.ZERO;
		for (SaleItem it : items) {
			it.recalculate();
			sum = sum.add(it.getTotal());
		}
		this.totalAmount = sum.setScale(2, RoundingMode.HALF_UP);
	}

	public UUID getId() { return id; }
	public String getSaleNumber() { return saleNumber; }
	public LocalDateTime getSaleDate() { return saleDate; }
	public String getClientExternalId() { return clientExternalId; }
	public String getClientName() { return clientName; }
	public String getBranchExternalId() { return branchExternalId; }
	public String getBranchName() { return branchName; }
	public List<SaleItem> getItems() { return Collections.unmodifiableList(items); }
	public BigDecimal getTotalAmount() { return totalAmount; }
	public SaleStatus getStatus() { return status; }

	public void setSaleDate(LocalDateTime saleDate) { this.saleDate = Objects.requireNonNull(saleDate); }
	public void setClient(String clientExternalId, String clientName) {
		this.clientExternalId = Objects.requireNonNull(clientExternalId);
		this.clientName = Objects.requireNonNull(clientName);
	}
	public void setBranch(String branchExternalId, String branchName) {
		this.branchExternalId = Objects.requireNonNull(branchExternalId);
		this.branchName = Objects.requireNonNull(branchName);
	}
}
