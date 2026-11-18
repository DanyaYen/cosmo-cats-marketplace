package com.cosmocats.marketplace.repository;

import com.cosmocats.marketplace.entity.OrderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @EntityGraph(attributePaths = {"items"})
    List<OrderEntity> findByUserId(UUID userId);
}
