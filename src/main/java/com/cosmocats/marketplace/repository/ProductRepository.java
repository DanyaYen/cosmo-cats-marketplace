package com.cosmocats.marketplace.repository;

import com.cosmocats.marketplace.entity.ProductEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"category"})
    List<ProductEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"category"})
    Optional<ProductEntity> findById(UUID id);

    @EntityGraph(attributePaths = {"category"})
    List<ProductEntity> findByCategoryNameIgnoreCase(String categoryName);
}
