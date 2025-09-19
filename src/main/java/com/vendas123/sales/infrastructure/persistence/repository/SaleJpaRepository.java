package com.vendas123.sales.infrastructure.persistence.repository;

import com.vendas123.sales.infrastructure.persistence.entity.SaleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {
	List<SaleEntity> findAllByOrderBySaleDateDesc(Pageable pageable);
}
