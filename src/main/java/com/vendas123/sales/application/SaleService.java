package com.vendas123.sales.application;

import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.model.SaleItem;
import com.vendas123.sales.domain.model.SaleStatus;
import com.vendas123.sales.domain.ports.SaleRepository;
import com.vendas123.sales.domain.ports.EventPublisher;
import com.vendas123.shared.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vendas123.shared.exception.BusinessException;

@Service
public class SaleService {
	private final SaleRepository saleRepository;
	private final EventPublisher events;

	public SaleService(SaleRepository saleRepository, EventPublisher events) {
		this.saleRepository = saleRepository;
		this.events = events;
	}

	@Transactional
	public Sale create(String clientId, String clientName,
	                   String branchId, String branchName,
	                   LocalDateTime saleDate,
	                   List<SaleItem> items) {
		String saleNumber = generateSaleNumber();
		Sale sale = Sale.create(saleNumber, saleDate, clientId, clientName, branchId, branchName, items);
		Sale saved = saleRepository.save(sale);
		events.publish("CompraEfetuada", saved);
		return saved;
	}

	@Transactional(readOnly = true)
	public Sale get(UUID id) {
		return saleRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Sale not found: " + id));
	}

	@Transactional(readOnly = true)
	public List<Sale> list(int page, int size) {
		return saleRepository.findAll(page, size);
	}

	@Transactional
	public Sale update(UUID id, String clientId, String clientName,
	                   String branchId, String branchName,
	                   LocalDateTime saleDate,
	                   List<SaleItem> newItems) {
		Sale sale = get(id);
		var beforeItems = sale.getItems();
		sale.setClient(clientId, clientName);
		sale.setBranch(branchId, branchName);
		sale.setSaleDate(saleDate != null ? saleDate : sale.getSaleDate());
		sale.replaceItems(newItems);
		Sale saved = saleRepository.save(sale);
		// Detect removed items -> ItemCancelado events
		var beforeSet = beforeItems.stream().map(i -> i.getProductExternalId()).collect(java.util.stream.Collectors.toSet());
		var afterSet = saved.getItems().stream().map(i -> i.getProductExternalId()).collect(java.util.stream.Collectors.toSet());
		for (String removed : beforeSet) {
			if (!afterSet.contains(removed)) {
				events.publish("ItemCancelado", java.util.Map.of("saleId", saved.getId(), "productExternalId", removed));
			}
		}
		events.publish("CompraAlterada", saved);
		return saved;
	}

	@Transactional
	public Sale cancel(UUID id) {
		Sale sale = get(id);
		if (sale.getStatus() == SaleStatus.CANCELLED) return sale;
		sale.cancel();
		Sale saved = saleRepository.save(sale);
		events.publish("CompraCancelada", saved);
		return saved;
	}

	@Transactional
	public void delete(UUID id) {
		Sale sale = get(id); // ensure exists
		if (sale.getStatus() != SaleStatus.CANCELLED) {
			throw new BusinessException("Only cancelled sales can be deleted");
		}
		saleRepository.deleteById(id);
	}

	private String generateSaleNumber() {
		// Simple, unique enough: S-<epoch>-<count+1>
		long seq = saleRepository.count() + 1;
		return "S-" + System.currentTimeMillis() + "-" + seq;
	}
}
