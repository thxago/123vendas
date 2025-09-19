package com.vendas123.sales.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public class SaleRequest {
	@NotBlank
	public String clientExternalId;
	@NotBlank
	public String clientName;

	@NotBlank
	public String branchExternalId;
	@NotBlank
	public String branchName;

	public LocalDateTime saleDate; // optional

	@NotEmpty
	@Valid
	public List<SaleItemRequest> items;
}
