package com.vendas123.sales.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class SaleItemRequest {
	@NotBlank
	public String productExternalId;
	@NotBlank
	public String productName;
	@Min(1) @Max(20)
	public int quantity;
	@NotNull @DecimalMin(value = "0.01")
	public BigDecimal unitPrice;
}
