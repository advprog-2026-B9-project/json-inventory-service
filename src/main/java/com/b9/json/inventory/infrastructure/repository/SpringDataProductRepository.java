package com.b9.json.inventory.infrastructure.repository;

import com.b9.json.inventory.domain.model.Product;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByOwnerId(UUID ownerId);

    @Query("SELECT p FROM Product p WHERE " +
            "(CAST(:name AS String) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS String), '%'))) AND " +
            "(CAST(:jastiperId AS UUID) IS NULL OR p.ownerId = :jastiperId)")
    List<Product> searchProducts(@Param("name") String name, @Param("jastiperId") UUID jastiperId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") UUID id);
}