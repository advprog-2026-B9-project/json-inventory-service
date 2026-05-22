package com.b9.json.inventory.domain.repository;

import com.b9.json.inventory.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findByOwnerId(UUID ownerId);
    List<Product> searchProducts(String name, UUID jastiperId);
    void deleteById(UUID id);
    Optional<Product> findByIdForUpdate(UUID id);
}