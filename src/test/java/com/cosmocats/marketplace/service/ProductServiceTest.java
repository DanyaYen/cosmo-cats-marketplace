package com.cosmocats.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should persist new product in repository and return response DTO")
    void createProduct_ValidRequest_SavesAndReturnsDto() {
        ProductRequestDto request = new ProductRequestDto(
                "Starship Carrier",
                "Ventilated travel capsule",
                new BigDecimal("89.99"),
                "Travel"
        );
        UUID categoryId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CategoryEntity category = CategoryEntity.builder().id(categoryId).name("Travel").build();
        Product mappedDomain = Product.builder().name("Starship Carrier").price(new BigDecimal("89.99")).categoryName("Travel").build();
        ProductEntity toSave = ProductEntity.builder().name("Starship Carrier").price(new BigDecimal("89.99")).build();
        ProductEntity savedEntity = ProductEntity.builder().id(productId).name("Starship Carrier").price(new BigDecimal("89.99")).category(category).build();
        Product savedDomain = Product.builder().id(productId).name("Starship Carrier").price(new BigDecimal("89.99")).categoryName("Travel").build();
        ProductResponseDto expectedDto = new ProductResponseDto(productId, "Starship Carrier", "Ventilated travel capsule", new BigDecimal("89.99"), "Travel");

        when(categoryRepository.findByNameIgnoreCase("Travel")).thenReturn(Optional.of(category));
        when(productMapper.toDomain(request)).thenReturn(mappedDomain);
        when(productEntityMapper.toEntity(mappedDomain)).thenReturn(toSave);
        when(productRepository.save(toSave)).thenReturn(savedEntity);
        when(productEntityMapper.toDomain(savedEntity)).thenReturn(savedDomain);
        when(productMapper.toResponseDto(savedDomain)).thenReturn(expectedDto);

        ProductResponseDto result = productService.createProduct(request);

        assertThat(result.id()).isEqualTo(productId);
        assertThat(result.name()).isEqualTo("Starship Carrier");
        verify(productRepository).save(toSave);
    }

    @Test
    @DisplayName("Should return all products mapped from repository")
    void getAllProducts_ReturnsMappedList() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = ProductEntity.builder().id(id).name("Astro Toy").build();
        Product domain = Product.builder().id(id).name("Astro Toy").build();
        ProductResponseDto dto = new ProductResponseDto(id, "Astro Toy", "Desc", BigDecimal.TEN, "Toys");

        when(productRepository.findAll()).thenReturn(List.of(entity));
        when(productEntityMapper.toDomain(entity)).thenReturn(domain);
        when(productMapper.toResponseDto(domain)).thenReturn(dto);

        List<ProductResponseDto> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Astro Toy");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product ID is missing")
    void getProductById_MissingId_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(randomId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(randomId.toString());
    }

    @Test
    @DisplayName("Should update existing product in repository")
    void updateProduct_ExistingId_UpdatesAndSaves() {
        UUID id = UUID.randomUUID();
        CategoryEntity category = CategoryEntity.builder().name("Toys").build();
        ProductEntity existing = ProductEntity.builder().id(id).name("Old Star").category(category).build();
        ProductRequestDto updateReq = new ProductRequestDto("Supernova Toy", "Updated desc", new BigDecimal("15.50"), "Toys");
        Product updatedDomain = Product.builder().id(id).name("Supernova Toy").price(new BigDecimal("15.50")).categoryName("Toys").build();
        ProductResponseDto expectedDto = new ProductResponseDto(id, "Supernova Toy", "Updated desc", new BigDecimal("15.50"), "Toys");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByNameIgnoreCase("Toys")).thenReturn(Optional.of(category));
        when(productRepository.save(existing)).thenReturn(existing);
        when(productEntityMapper.toDomain(existing)).thenReturn(updatedDomain);
        when(productMapper.toResponseDto(updatedDomain)).thenReturn(expectedDto);

        ProductResponseDto result = productService.updateProduct(id, updateReq);

        assertThat(result.name()).isEqualTo("Supernova Toy");
        verify(productRepository).save(existing);
    }

    @Test
    @DisplayName("Should delete product when it exists and throw when missing")
    void deleteProduct_ChecksExistenceBeforeDelete() {
        UUID existingId = UUID.randomUUID();
        UUID missingId = UUID.randomUUID();

        when(productRepository.existsById(existingId)).thenReturn(true);
        when(productRepository.existsById(missingId)).thenReturn(false);

        productService.deleteProduct(existingId);
        verify(productRepository).deleteById(existingId);

        assertThatThrownBy(() -> productService.deleteProduct(missingId))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository, never()).deleteById(missingId);
    }
}
