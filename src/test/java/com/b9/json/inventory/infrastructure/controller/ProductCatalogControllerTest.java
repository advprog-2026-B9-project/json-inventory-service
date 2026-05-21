package com.b9.json.inventory.infrastructure.controller;

import com.b9.json.inventory.application.dto.ProductDetailResponse;
import com.b9.json.inventory.application.service.ProductCatalogService;
import com.b9.json.inventory.domain.model.Product;

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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProductCatalogController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class ProductCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCatalogService catalogService;

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
                .ownerUsername("user1")
                .build();
    }

    @Test
    void getAllProducts_WithNoParams_Success() throws Exception {
        ProductDetailResponse response = new ProductDetailResponse(sampleProduct, "User 1", "08123456789");
        List<ProductDetailResponse> responses = Collections.singletonList(response);

        when(catalogService.getAllProductsWithDetails(null, null)).thenReturn(responses);

        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$[0].jastiperFullName").value("User 1"));

        verify(catalogService, times(1)).getAllProductsWithDetails(null, null);
    }

    @Test
    void getAllProducts_WithParams_Success() throws Exception {
        ProductDetailResponse response = new ProductDetailResponse(sampleProduct, "User 1", "08123456789");
        List<ProductDetailResponse> responses = Collections.singletonList(response);

        when(catalogService.getAllProductsWithDetails("produk", "user1")).thenReturn(responses);

        mockMvc.perform(get("/api/v1/products")
                        .param("name", "produk")
                        .param("jastiper", "user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$[0].jastiperUsername").value("user1"));

        verify(catalogService, times(1)).getAllProductsWithDetails("produk", "user1");
    }

    @Test
    void getProductById_Success() throws Exception {
        when(catalogService.getProductById(productId)).thenReturn(sampleProduct);

        mockMvc.perform(get("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()));

        verify(catalogService, times(1)).getProductById(productId);
    }

    @Test
    void addProductRating_Success() throws Exception {
        doNothing().when(catalogService).addProductRating(productId, 5);

        mockMvc.perform(post("/api/v1/products/{id}/rating", productId)
                        .param("ratingScore", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(catalogService, times(1)).addProductRating(productId, 5);
    }
}