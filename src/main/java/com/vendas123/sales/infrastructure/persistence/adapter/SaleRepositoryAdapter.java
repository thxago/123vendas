package com.vendas123.sales.infrastructure.persistence.adapter;

import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.ports.SaleRepository;
import com.vendas123.sales.infrastructure.persistence.entity.SaleEntity;
import com.vendas123.sales.infrastructure.persistence.repository.SaleJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SaleRepositoryAdapter implements SaleRepository {
	private final SaleJpaRepository jpa;

	public SaleRepositoryAdapter(SaleJpaRepository jpa) {
		this.jpa = jpa;
	}

	@Override
	public Sale save(Sale sale) {
		SaleEntity entity = SaleEntity.fromDomain(sale);
		return jpa.save(entity).toDomain();
	}

	@Override
	public Optional<Sale> findById(UUID id) {
		return jpa.findById(id).map(SaleEntity::toDomain);
	}

	@Override
	public void deleteById(UUID id) {
		jpa.deleteById(id);
	}

	@Override
	public List<Sale> findAll(int page, int size) {
		return jpa.findAllByOrderBySaleDateDesc(PageRequest.of(page, size))
				.stream().map(SaleEntity::toDomain).toList();
	}

	@Override
	public long count() {
		return jpa.count();
	}
}
