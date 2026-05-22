package com.b9.json.inventory.application.service;

import com.b9.json.inventory.application.dto.ProductDetailResponse;
import com.b9.json.inventory.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductCatalogService {
    List<Product> getAllProducts();
    Product getProductById(UUID id);
    List<ProductDetailResponse> getAllProductsWithDetails(String name, UUID jastiperId);
    void addProductRating(UUID id, Integer ratingScore);
}
