package com.b9.json.inventory.infrastructure.controller;

import com.b9.json.inventory.application.service.AdminProductService;
import com.b9.json.inventory.domain.model.Product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AdminProductController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminProductService adminService;

    private Product sampleProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sampleProduct = Product.builder()
                .id(productId)
                .name("produk 1")
                .description("beli bang")
                .price(new BigDecimal("50000"))
                .stock(3)
                .originCountry("Indo")
                .arrivalDate(LocalDate.now().plusDays(7))
                .ownerUsername("admin_user")
                .build();
    }

    @Test
    void testAdminUpdateProduct_Success() throws Exception {
        when(adminService.adminUpdateProduct(eq(productId), any(Product.class))).thenReturn(sampleProduct);

        mockMvc.perform(put("/api/v1/products/admin/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()));

        verify(adminService, times(1)).adminUpdateProduct(eq(productId), any(Product.class));
    }

    @Test
    void testAdminDeleteProduct_Success() throws Exception {
        doNothing().when(adminService).adminDeleteProduct(productId);

        mockMvc.perform(delete("/api/v1/products/admin/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).adminDeleteProduct(productId);
    }
}