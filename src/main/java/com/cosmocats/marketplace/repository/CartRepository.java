package com.cosmocats.marketplace.repository;

import com.cosmocats.marketplace.entity.CartEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, UUID> {

    @EntityGraph(attributePaths = {"items"})
    Optional<CartEntity> findByUserId(UUID userId);
}
