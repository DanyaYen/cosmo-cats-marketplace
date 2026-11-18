package com.cosmocats.marketplace.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.dto.ProductRequestDto;
import com.cosmocats.marketplace.dto.ProductResponseDto;
import com.cosmocats.marketplace.entity.CategoryEntity;
import com.cosmocats.marketplace.entity.ProductEntity;
import com.cosmocats.marketplace.exception.ProductNotFoundException;
import com.cosmocats.marketplace.mapper.ProductEntityMapper;
import com.cosmocats.marketplace.mapper.ProductMapper;
import com.cosmocats.marketplace.repository.CategoryRepository;
import com.cosmocats.marketplace.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductEntityMapper productEntityMapper;

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productEntityMapper::toDomain)
                .map(productMapper::toResponseDto)
                .toList();
    }

    public ProductResponseDto getProductById(UUID id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponseDto(productEntityMapper.toDomain(entity));
    }

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        CategoryEntity category = resolveCategory(requestDto.categoryName());
        Product domainProduct = productMapper.toDomain(requestDto);
        ProductEntity entity = productEntityMapper.toEntity(domainProduct);
        entity.setCategory(category);

        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponseDto(productEntityMapper.toDomain(saved));
    }

    @Transactional
    public ProductResponseDto updateProduct(UUID id, ProductRequestDto requestDto) {
        ProductEntity existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        CategoryEntity category = resolveCategory(requestDto.categoryName());

        existing.setName(requestDto.name());
        existing.setDescription(requestDto.description());
        existing.setPrice(requestDto.price());
        existing.setCategory(category);

        ProductEntity saved = productRepository.save(existing);
        return productMapper.toResponseDto(productEntityMapper.toDomain(saved));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private CategoryEntity resolveCategory(String categoryName) {
        String normalized = categoryName.trim();
        return categoryRepository.findByNameIgnoreCase(normalized)
                .orElseGet(() -> categoryRepository.save(
                        CategoryEntity.builder()
                                .name(normalized)
                                .description("Cosmic category: " + normalized)
                                .build()
                ));
    }
}
