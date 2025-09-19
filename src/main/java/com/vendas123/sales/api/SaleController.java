package com.vendas123.sales.api;

import com.vendas123.sales.api.dto.SaleRequest;
import com.vendas123.sales.api.dto.SaleResponse;
import com.vendas123.sales.api.mapper.SaleMapper;
import com.vendas123.sales.application.SaleService;
import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.ports.SaleRepository;
import com.vendas123.sales.infrastructure.persistence.adapter.SaleRepositoryAdapter;
import com.vendas123.sales.infrastructure.persistence.repository.SaleJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {
	private final SaleService service;

	public SaleController(SaleService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<SaleResponse> create(@Validated @RequestBody SaleRequest req) {
		Sale sale = service.create(
				req.clientExternalId, req.clientName,
				req.branchExternalId, req.branchName,
				req.saleDate,
				SaleMapper.toDomainItems(req.items)
		);
		SaleResponse resp = SaleMapper.toResponse(sale);
		return ResponseEntity.created(URI.create("/api/v1/sales/" + resp.id)).body(resp);
	}

	@GetMapping("{id}")
	public SaleResponse get(@PathVariable UUID id) {
		return SaleMapper.toResponse(service.get(id));
	}

	@GetMapping
	public List<SaleResponse> list(@RequestParam(defaultValue = "0") int page,
	                               @RequestParam(defaultValue = "20") int size) {
		return service.list(page, size).stream().map(SaleMapper::toResponse).toList();
	}

	@PutMapping("{id}")
	public SaleResponse update(@PathVariable UUID id, @Validated @RequestBody SaleRequest req) {
		Sale sale = service.update(
				id,
				req.clientExternalId, req.clientName,
				req.branchExternalId, req.branchName,
				req.saleDate,
				SaleMapper.toDomainItems(req.items)
		);
		return SaleMapper.toResponse(sale);
	}

	@PatchMapping("{id}/cancel")
	public SaleResponse cancel(@PathVariable UUID id) {
		return SaleMapper.toResponse(service.cancel(id));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@Configuration
	static class Beans {
		@Bean
		SaleService saleService(SaleRepository saleRepository) {
			return new SaleService(saleRepository);
		}

		@Bean
		SaleRepository saleRepositoryAdapter(SaleJpaRepository jpa) {
			return new SaleRepositoryAdapter(jpa);
		}
	}
}
