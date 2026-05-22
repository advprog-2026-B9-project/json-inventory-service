package com.b9.json.inventory.application.service;

import com.b9.json.inventory.domain.model.Product;

import java.util.UUID;

public interface AdminProductService {
    void adminDeleteProduct(UUID id);
    Product adminUpdateProduct(UUID id, Product updatedProduct);
}