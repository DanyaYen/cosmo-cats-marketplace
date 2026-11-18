package com.cosmocats.marketplace.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.dto.ProductRequestDto;
import com.cosmocats.marketplace.dto.ProductResponseDto;
import com.cosmocats.marketplace.exception.ProductNotFoundException;
import com.cosmocats.marketplace.mapper.ProductMapper;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final Map<UUID, Product> productStore = new ConcurrentHashMap<>();
    private final ProductMapper productMapper;

    @PostConstruct
    void initMockData() {
        Product p1 = Product.builder()
                .id(UUID.randomUUID())
                .name("Cosmic Laser Pointer")
                .description("Zero-gravity red dot laser for energetic space kittens")
                .price(new BigDecimal("19.99"))
                .categoryName("Toys")
                .build();
        Product p2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Nebula Catnip Crunchies")
                .description("Freeze-dried organic catnip harvested in the Orion Nebula")
                .price(new BigDecimal("9.50"))
                .categoryName("Treats")
                .build();
        productStore.put(p1.getId(), p1);
        productStore.put(p2.getId(), p2);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productStore.values().stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    public ProductResponseDto getProductById(UUID id) {
        Product product = Optional.ofNullable(productStore.get(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponseDto(product);
    }

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = productMapper.toDomain(requestDto);
        product.setId(UUID.randomUUID());
        productStore.put(product.getId(), product);
        return productMapper.toResponseDto(product);
    }

    public ProductResponseDto updateProduct(UUID id, ProductRequestDto requestDto) {
        if (!productStore.containsKey(id)) {
            throw new ProductNotFoundException(id);
        }
        Product updated = productMapper.toDomain(requestDto);
        updated.setId(id);
        productStore.put(id, updated);
        return productMapper.toResponseDto(updated);
    }

    public void deleteProduct(UUID id) {
        if (productStore.remove(id) == null) {
            throw new ProductNotFoundException(id);
        }
    }
}
