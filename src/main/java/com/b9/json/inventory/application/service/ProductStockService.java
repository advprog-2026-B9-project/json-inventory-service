package com.b9.json.inventory.application.service;

import java.util.UUID;

public interface ProductStockService {
    void deductProductStock(UUID id, Integer quantity);
    void increaseProductStock(UUID id, Integer quantity);
}
