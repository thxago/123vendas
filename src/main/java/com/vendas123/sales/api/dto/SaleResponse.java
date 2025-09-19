package com.vendas123.sales.api.dto;

import com.vendas123.sales.domain.model.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SaleResponse {
	public UUID id;
	public String saleNumber;
	public LocalDateTime saleDate;
	public String clientExternalId;
	public String clientName;
	public String branchExternalId;
	public String branchName;
	public BigDecimal totalAmount;
	public SaleStatus status;
	public List<SaleItemResponse> items;
}
