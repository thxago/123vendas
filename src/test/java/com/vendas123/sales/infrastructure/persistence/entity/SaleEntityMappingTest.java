package com.vendas123.sales.infrastructure.persistence.entity;

import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.model.SaleItem;
import com.vendas123.sales.domain.model.SaleStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleEntityMappingTest {

    @Test
    void round_trip_domain_to_entity_and_back() {
        SaleItem i1 = new SaleItem("P1","Prod 1", 5, new BigDecimal("10.00"));
        SaleItem i2 = new SaleItem("P2","Prod 2", 2, new BigDecimal("3.50"));
        Sale sale = Sale.create("S-1", LocalDateTime.now(), "C1","Client","B1","Branch", List.of(i1, i2));

        SaleEntity entity = SaleEntity.fromDomain(sale);
        assertNotNull(entity);
        assertEquals(sale.getId(), entity.toDomain().getId());

        Sale back = entity.toDomain();
        assertEquals(sale.getSaleNumber(), back.getSaleNumber());
        assertEquals(sale.getClientExternalId(), back.getClientExternalId());
        assertEquals(sale.getBranchExternalId(), back.getBranchExternalId());
        assertEquals(sale.getStatus(), back.getStatus());
        assertEquals(sale.getTotalAmount(), back.getTotalAmount());
        assertEquals(2, back.getItems().size());
        assertEquals("P1", back.getItems().get(0).getProductExternalId());
        assertEquals("P2", back.getItems().get(1).getProductExternalId());
    }
}
