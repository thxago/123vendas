package com.vendas123.sales.api.mapper;

import com.vendas123.sales.api.dto.*;
import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.model.SaleItem;

import java.util.List;

public final class SaleMapper {
	private SaleMapper() {}

	public static List<SaleItem> toDomainItems(List<SaleItemRequest> items) {
		return items.stream()
				.map(i -> new SaleItem(i.productExternalId, i.productName, i.quantity, i.unitPrice))
				.toList();
	}

	public static SaleResponse toResponse(Sale sale) {
		SaleResponse r = new SaleResponse();
		r.id = sale.getId();
		r.saleNumber = sale.getSaleNumber();
		r.saleDate = sale.getSaleDate();
		r.clientExternalId = sale.getClientExternalId();
		r.clientName = sale.getClientName();
		r.branchExternalId = sale.getBranchExternalId();
		r.branchName = sale.getBranchName();
		r.totalAmount = sale.getTotalAmount();
		r.status = sale.getStatus();
		r.items = sale.getItems().stream().map(SaleMapper::toItemResponse).toList();
		return r;
	}

	private static SaleItemResponse toItemResponse(SaleItem it) {
		SaleItemResponse r = new SaleItemResponse();
		r.productExternalId = it.getProductExternalId();
		r.productName = it.getProductName();
		r.quantity = it.getQuantity();
		r.unitPrice = it.getUnitPrice();
		r.discount = it.getDiscount();
		r.total = it.getTotal();
		return r;
	}
}
