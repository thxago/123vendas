package com.vendas123.sales.application;

import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.model.SaleItem;
import com.vendas123.sales.domain.ports.SaleRepository;
import com.vendas123.sales.domain.ports.EventPublisher;
import com.vendas123.shared.exception.NotFoundException;
import com.vendas123.shared.exception.BusinessException;
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
    private EventPublisher events;
    private SaleService service;

    @BeforeEach
    void setUp() {
    repo = mock(SaleRepository.class);
    events = mock(EventPublisher.class);
    service = new SaleService(repo, events);
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
        verify(events).publish(eq("CompraEfetuada"), any());
    }

    @Test
    void get_throws_when_notFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(id));
    }

    @Test
    void get_returns_sale_when_found() {
        Sale existing = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch", List.of(item(1, "10.00")));
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
        Sale got = service.get(existing.getId());
        assertEquals(existing.getId(), got.getId());
    }

    @Test
    void list_delegates_to_repo() {
        when(repo.findAll(0, 20)).thenReturn(List.of());
        List<Sale> result = service.list(0, 20);
        assertNotNull(result);
        verify(repo).findAll(0, 20);
    }

    @Test
    void update_replaces_items_and_saves() {
    Sale existing = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch",
        List.of(new SaleItem("P1","Prod 1", 2, new BigDecimal("10.00")),
            new SaleItem("P2","Prod 2", 1, new BigDecimal("5.00"))));
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Remove P1, mantém apenas P2 -> deve publicar ItemCancelado e CompraAlterada
    Sale updated = service.update(existing.getId(), "C2","Client2","B2","Branch2", existing.getSaleDate(),
        List.of(new SaleItem("P2","Prod 2", 3, new BigDecimal("5.00"))));
        assertEquals("C2", updated.getClientExternalId());
    assertEquals(1, updated.getItems().size());
        verify(repo).save(any(Sale.class));
    verify(events).publish(eq("ItemCancelado"), any());
    verify(events).publish(eq("CompraAlterada"), any());
    }

    @Test
    void cancel_marks_cancelled() {
        Sale existing = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch", List.of(item(1, "10.00")));
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Sale cancelled = service.cancel(existing.getId());
        assertEquals(com.vendas123.sales.domain.model.SaleStatus.CANCELLED, cancelled.getStatus());
        verify(events).publish(eq("CompraCancelada"), any());
    }

    @Test
    void delete_active_throws_business_exception() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(Sale.create("S-1", LocalDateTime.now(), "C","N","B","BN", List.of(item(1, "1.00")))));
        assertThrows(BusinessException.class, () -> service.delete(id));
        verify(repo, never()).deleteById(any());
    }

    @Test
    void delete_when_cancelled_deletes() {
        UUID id = UUID.randomUUID();
        Sale s = Sale.create("S-1", LocalDateTime.now(), "C","N","B","BN", List.of(item(1, "1.00")));
        s.cancel();
        when(repo.findById(id)).thenReturn(Optional.of(s));
        service.delete(id);
        verify(repo).deleteById(id);
    }
}
