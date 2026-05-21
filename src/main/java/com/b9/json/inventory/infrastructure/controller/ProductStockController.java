package com.b9.json.inventory.infrastructure.controller;

import com.b9.json.inventory.application.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductStockController {

    private final ProductStockService stockService;

    @PutMapping("/{id}/deduct-stock")
    public ResponseEntity<Void> deductProductStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity) {
        stockService.deductProductStock(id, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/increase-stock")
    public ResponseEntity<Void> increaseProductStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity) {
        stockService.increaseProductStock(id, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}