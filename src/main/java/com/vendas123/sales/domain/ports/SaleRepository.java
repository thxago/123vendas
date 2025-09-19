package com.vendas123.sales.domain.ports;

import com.vendas123.sales.domain.model.Sale;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository {
	Sale save(Sale sale);
	Optional<Sale> findById(UUID id);
	void deleteById(UUID id);
	List<Sale> findAll(int page, int size);
	long count();
}
