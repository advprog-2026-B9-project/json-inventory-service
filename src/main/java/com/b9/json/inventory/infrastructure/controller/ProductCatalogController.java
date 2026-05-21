package com.b9.json.inventory.infrastructure.controller;

import com.b9.json.inventory.application.dto.ProductDetailResponse;
import com.b9.json.inventory.application.service.ProductCatalogService;
import com.b9.json.inventory.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductCatalogController {

    private final ProductCatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<ProductDetailResponse>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String jastiper) {
        return ResponseEntity.ok(catalogService.getAllProductsWithDetails(name, jastiper));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Product product = catalogService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<Void> addProductRating(
            @PathVariable UUID id,
            @RequestParam Integer ratingScore) {
        catalogService.addProductRating(id, ratingScore);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}