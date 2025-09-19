package com.vendas123.sales.api;

import com.vendas123.sales.api.dto.SaleItemRequest;
import com.vendas123.sales.api.mapper.SaleMapper;
import com.vendas123.sales.domain.model.Sale;
import com.vendas123.sales.domain.model.SaleItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleMapperTest {

    @Test
    void maps_request_items_and_response_correctly() {
        // toDomainItems
        SaleItemRequest req = new SaleItemRequest();
        req.productExternalId = "P1";
        req.productName = "Prod";
        req.quantity = 4;
        req.unitPrice = new BigDecimal("10.00");
        List<SaleItem> items = SaleMapper.toDomainItems(List.of(req));
        assertEquals(1, items.size());
        assertEquals("P1", items.get(0).getProductExternalId());

        // toResponse
        Sale sale = Sale.create("S-1", LocalDateTime.now(), "C","Client","B","Branch", items);
        var resp = SaleMapper.toResponse(sale);
        assertEquals(sale.getId(), resp.id);
        assertEquals(1, resp.items.size());
        assertEquals("P1", resp.items.get(0).productExternalId);
    }
}
