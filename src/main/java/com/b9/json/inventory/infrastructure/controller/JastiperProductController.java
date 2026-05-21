package com.b9.json.inventory.infrastructure.controller;

import com.b9.json.inventory.application.service.JastiperProductService;
import com.b9.json.inventory.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class JastiperProductController {

    private final JastiperProductService jastiperService;

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Validated @RequestBody Product product,
            @RequestHeader("X-User-Name") String ownerUsername) {
        Product createdProduct = jastiperService.createProduct(product, ownerUsername);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Product>> getMyProducts(
            @RequestHeader("X-User-Name") String ownerUsername) {
        List<Product> products = jastiperService.getMyProducts(ownerUsername);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable UUID id,
            @Validated @RequestBody Product product,
            @RequestHeader("X-User-Name") String ownerUsername) {
        Product updatedProduct = jastiperService.updateProduct(id, product, ownerUsername);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID id,
            @RequestHeader("X-User-Name") String ownerUsername) {
        jastiperService.deleteProduct(id, ownerUsername);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}