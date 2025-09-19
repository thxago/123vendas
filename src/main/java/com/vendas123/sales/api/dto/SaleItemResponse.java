package com.vendas123.sales.api.dto;

import java.math.BigDecimal;

public class SaleItemResponse {
	public String productExternalId;
	public String productName;
	public int quantity;
	public BigDecimal unitPrice;
	public BigDecimal discount;
	public BigDecimal total;
}
