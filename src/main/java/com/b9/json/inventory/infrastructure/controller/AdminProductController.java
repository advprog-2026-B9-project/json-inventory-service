package com.b9.json.inventory.infrastructure.controller;

import com.b9.json.inventory.application.service.AdminProductService;
import com.b9.json.inventory.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminService;

    @PutMapping("/{id}")
    public ResponseEntity<Product> adminUpdateProduct(
            @PathVariable UUID id,
            @Validated @RequestBody Product updatedData) {
        Product product = adminService.adminUpdateProduct(id, updatedData);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> adminDeleteProduct(@PathVariable UUID id) {
        adminService.adminDeleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}