package com.b9.json.inventory.application.service;

import com.b9.json.inventory.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface JastiperProductService {
    Product createProduct(Product product, String ownerUsername);
    Product updateProduct(UUID id, Product product, String ownerUsername);
    List<Product> getMyProducts(String ownerUsername);
    void deleteProduct(UUID id, String ownerUsername);
}