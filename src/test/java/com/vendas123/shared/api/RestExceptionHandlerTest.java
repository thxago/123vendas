package com.vendas123.shared.api;

import com.vendas123.sales.api.SaleController;
import com.vendas123.sales.application.SaleService;
import com.vendas123.shared.exception.BusinessException;
import com.vendas123.shared.exception.NotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SaleController.class)
@Import(RestExceptionHandler.class)
class RestExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SaleService service;

    @Test
    void notFound_returns_404() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.get(id)).thenThrow(new NotFoundException("not found"));
        mockMvc.perform(get("/api/v1/sales/" + id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void business_returns_422_on_delete_active() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doThrow(new BusinessException("Only cancelled sales can be deleted"))
            .when(service).delete(id);
        mockMvc.perform(delete("/api/v1/sales/" + id))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void validation_returns_400_on_missing_fields() throws Exception {
        // Missing required fields like clientExternalId -> 400
        String badBody = "{\n  \"items\": []\n}";
        mockMvc.perform(post("/api/v1/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void typeMismatch_returns_400() throws Exception {
        mockMvc.perform(get("/api/v1/sales/not-a-uuid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void unreadableJson_returns_400() throws Exception {
        mockMvc.perform(post("/api/v1/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ this is not json }"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void dataIntegrityViolation_returns_422() throws Exception {
        when(service.create(any(), any(), any(), any(), any(), any()))
            .thenThrow(new DataIntegrityViolationException("dup"));
        String goodBody = "{\n  \"clientExternalId\": \"C1\",\n  \"clientName\": \"Client\",\n  \"branchExternalId\": \"B1\",\n  \"branchName\": \"Branch\",\n  \"items\": [ { \n    \"productExternalId\": \"P1\", \n    \"productName\": \"Prod\", \n    \"quantity\": 1, \n    \"unitPrice\": 10.00 \n  } ]\n}";
        mockMvc.perform(post("/api/v1/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(goodBody))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void genericException_returns_500() throws Exception {
        when(service.list(0,20)).thenThrow(new RuntimeException("boom"));
        mockMvc.perform(get("/api/v1/sales"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void bindException_returns_400() throws Exception {
        // Force Spring to throw BindException by binding invalid payload to SaleRequest via controller method
        // Here we simulate by directly invoking the endpoint with empty body but form content type
        RestExceptionHandler handler = new RestExceptionHandler();
        BindException be = new BindException(new Object(), "target");
        var resp = handler.handleValidation(be);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void typeMismatch_with_null_requiredType_returns_400() throws Exception {
        // Create a synthetic MethodArgumentTypeMismatchException and pass through handler directly
        RestExceptionHandler handler = new RestExceptionHandler();
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", null, "id", null, null);
        var resp = handler.handleTypeMismatch(ex);
        assertEquals(400, resp.getStatusCode().value());
    }
}
