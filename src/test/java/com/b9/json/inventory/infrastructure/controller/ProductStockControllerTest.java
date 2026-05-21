package com.b9.json.inventory.infrastructure.controller;

import com.b9.json.inventory.application.service.ProductStockService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProductStockController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class ProductStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductStockService stockService;

    @Test
    void deductProductStock_Success() throws Exception {
        UUID productId = UUID.randomUUID();
        doNothing().when(stockService).deductProductStock(productId, 2);

        mockMvc.perform(put("/api/v1/products/{id}/deduct-stock", productId)
                        .param("quantity", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(stockService, times(1)).deductProductStock(productId, 2);
    }

    @Test
    void increaseProductStock_Success() throws Exception {
        UUID productId = UUID.randomUUID();
        doNothing().when(stockService).increaseProductStock(productId, 3);

        mockMvc.perform(put("/api/v1/products/{id}/increase-stock", productId)
                        .param("quantity", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(stockService, times(1)).increaseProductStock(productId, 3);
    }
}