package com.vendas123.sales.application;

import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.model.SaleItem;
import com.vendas123.sales.domain.ports.SaleRepository;
import com.vendas123.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaleServiceTest {

    private SaleRepository repo;
    private SaleService service;

    @BeforeEach
    void setUp() {
        repo = mock(SaleRepository.class);
        service = new SaleService(repo);
    }

    private SaleItem item(int qty, String price) {
        return new SaleItem("P1", "Prod 1", qty, new BigDecimal(price));
    }

    @Test
    void create_saves_and_returns_sale() {
        when(repo.count()).thenReturn(0L);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Sale sale = service.create("C1","Client","B1","Branch", LocalDateTime.now(), List.of(item(1, "10.00")));
        assertNotNull(sale);
        verify(repo).save(any(Sale.class));
    }

    @Test
    void get_throws_when_notFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(id));
    }

    @Test
    void update_replaces_items_and_saves() {
        Sale existing = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch", List.of(item(1, "10.00")));
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Sale updated = service.update(existing.getId(), "C2","Client2","B2","Branch2", existing.getSaleDate(), List.of(item(2, "5.00")));
        assertEquals("C2", updated.getClientExternalId());
        assertEquals(1, updated.getItems().size());
        verify(repo).save(any(Sale.class));
    }

    @Test
    void cancel_marks_cancelled() {
        Sale existing = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch", List.of(item(1, "10.00")));
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Sale cancelled = service.cancel(existing.getId());
        assertEquals(com.vendas123.sales.domain.model.SaleStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void delete_calls_repo_deleteById() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(Sale.create("S-1", LocalDateTime.now(), "C","N","B","BN", List.of(item(1, "1.00")))));
        service.delete(id);
        verify(repo).deleteById(id);
    }
}
