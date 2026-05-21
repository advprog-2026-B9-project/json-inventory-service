package com.b9.json.inventory.application.service;

import com.b9.json.inventory.application.integration.AuthIntegrationService;
import com.b9.json.inventory.domain.model.Product;
import com.b9.json.inventory.application.exception.InsufficientStockException;
import com.b9.json.inventory.application.exception.InvalidRatingScoreException;
import com.b9.json.inventory.application.exception.InvalidStockQuantityException;
import com.b9.json.inventory.application.exception.ProductNotFoundException;
import com.b9.json.inventory.application.exception.ProductOwnershipException;
import com.b9.json.inventory.application.dto.ProductDetailResponse;
import com.b9.json.inventory.application.dto.UserDto;

import com.b9.json.inventory.domain.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements
        ProductCatalogService,
        JastiperProductService,
        ProductStockService,
        AdminProductService {

    private static final double ROUNDING_FACTOR = 100.0;

    private final ProductRepository productRepository;
    private final AuthIntegrationService authIntegrationService;

    @Override
    public Product createProduct(Product product, String ownerUsername) {
        product.setOwnerUsername(ownerUsername);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(UUID id, Product updatedData, String ownerUsername) {
        Product existingProduct = findProductByIdForUpdate(id);
        validateOwnership(existingProduct.getOwnerUsername(), ownerUsername);

        updateBasicFields(existingProduct, updatedData);

        return productRepository.save(existingProduct);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getMyProducts(String ownerUsername) {
        return productRepository.findByOwner(ownerUsername);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id, String ownerUsername) {
        Product product = findProductByIdForUpdate(id);
        validateOwnership(product.getOwnerUsername(), ownerUsername);

        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDetailResponse> getAllProductsWithDetails(String name, String jastiper) {
        List<Product> products = productRepository.searchProducts(name, jastiper);

        return products.stream().map(product -> {
            UserDto user = authIntegrationService.getUserByUsername(product.getOwnerUsername());

            String fullName = (user != null) ? user.fullName() : "Anonim";
            String phone = (user != null) ? user.phoneNumber() : "-";
            return new ProductDetailResponse(product, fullName, phone);
        }).toList();
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produk tidak ditemukan"));
    }

    @Override
    @Transactional
    public void deductProductStock(UUID id, Integer quantity) {
        validateQuantity(quantity, "pengurangan");

        Product product = findProductByIdForUpdate(id);
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Stok tidak mencukupi untuk produk: %s. Sisa stok: %d"
                            .formatted(product.getName(), product.getStock())
            );
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void increaseProductStock(UUID id, Integer quantity) {
        validateQuantity(quantity, "penambahan");

        Product product = findProductByIdForUpdate(id);
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void adminDeleteProduct(UUID id) {
        Product product = findProductByIdForUpdate(id);
        productRepository.deleteById(product.getId());
    }

    @Override
    @Transactional
    public Product adminUpdateProduct(UUID id, Product updatedProduct) {
        Product existingProduct = findProductByIdForUpdate(id);

        updateBasicFields(existingProduct, updatedProduct);
        existingProduct.setOriginCountry(updatedProduct.getOriginCountry());
        existingProduct.setArrivalDate(updatedProduct.getArrivalDate());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void addProductRating(UUID id, Integer ratingScore) {
        if (ratingScore == null || ratingScore < 1 || ratingScore > 5) {
            throw new InvalidRatingScoreException("Skor rating harus berada di antara 1 dan 5");
        }

        Product product = findProductByIdForUpdate(id);

        product.setTotalReviews(product.getTotalReviews() + 1);
        product.setTotalRatingScore(product.getTotalRatingScore() + ratingScore);

        double newAverage = (double) product.getTotalRatingScore() / product.getTotalReviews();
        newAverage = Math.round(newAverage * ROUNDING_FACTOR) / ROUNDING_FACTOR;
        product.setAverageRating(newAverage);

        productRepository.save(product);
    }

    private void updateBasicFields(Product existingProduct, Product updatedData) {
        existingProduct.setName(updatedData.getName());
        existingProduct.setDescription(updatedData.getDescription());
        existingProduct.setPrice(updatedData.getPrice());
        existingProduct.setStock(updatedData.getStock());
    }

    private void validateQuantity(Integer quantity, String actionType) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidStockQuantityException("Jumlah " + actionType + " stok harus lebih dari 0");
        }
    }

    private void validateOwnership(String productOwner, String requesterUsername) {
        if (!productOwner.equals(requesterUsername)) {
            throw new ProductOwnershipException("Anda tidak berhak memodifikasi produk ini");
        }
    }

    private Product findProductByIdForUpdate(UUID id){
        return productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ProductNotFoundException("Produk tidak ditemukan"));
    }
}