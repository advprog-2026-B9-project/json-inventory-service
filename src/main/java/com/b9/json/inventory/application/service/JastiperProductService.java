package com.b9.json.inventory.application.service;

import com.b9.json.inventory.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface JastiperProductService {
    Product createProduct(Product product, UUID ownerId);
    Product updateProduct(UUID id, Product product, UUID ownerId);
    List<Product> getMyProducts(UUID ownerId);
    void deleteProduct(UUID id, UUID ownerId);
}