package com.vendas123.sales.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendas123.sales.api.dto.SaleItemRequest;
import com.vendas123.sales.api.dto.SaleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SaleControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private SaleRequest sampleRequest() {
        SaleRequest r = new SaleRequest();
        r.clientExternalId = "C1";
        r.clientName = "Client 1";
        r.branchExternalId = "B1";
        r.branchName = "Branch 1";
        SaleItemRequest it = new SaleItemRequest();
        it.productExternalId = "P1";
        it.productName = "Prod 1";
        it.quantity = 5;
        it.unitPrice = new BigDecimal("10.00");
        r.items = List.of(it);
        return r;
    }

    @Test
    void full_crud_flow() throws Exception {
        // create
        String body = objectMapper.writeValueAsString(sampleRequest());
        String location = mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        // get
        mockMvc.perform(get(Objects.requireNonNull(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items[0].quantity").value(5))
                .andExpect(jsonPath("$.totalAmount").value("45.00"));

        // list
        mockMvc.perform(get("/api/v1/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        // cancel
        mockMvc.perform(patch(location + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // delete
        mockMvc.perform(delete(Objects.requireNonNull(location)))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_requires_cancelled_status() throws Exception {
        // create a sale
        String body = objectMapper.writeValueAsString(sampleRequest());
        String location = mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        // attempt delete while ACTIVE -> 422
        mockMvc.perform(delete(Objects.requireNonNull(location)))
                .andExpect(status().isUnprocessableEntity());

        // cancel then delete -> 204
        mockMvc.perform(patch(location + "/cancel"))
                .andExpect(status().isOk());
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }
}
