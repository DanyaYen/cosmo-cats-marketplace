package com.cosmocats.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.dto.ProductRequestDto;
import com.cosmocats.marketplace.dto.ProductResponseDto;
import com.cosmocats.marketplace.exception.ProductNotFoundException;
import com.cosmocats.marketplace.mapper.ProductMapper;
import java.math.BigDecimal;
import java.util.List;
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
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should create product, assign UUID and return response DTO")
    void createProduct_ValidRequest_ReturnsCreatedProduct() {
        ProductRequestDto request = new ProductRequestDto(
                "Starship Carrier",
                "Ventilated travel capsule",
                new BigDecimal("89.99"),
                "Travel"
        );
        Product domainProduct = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .categoryName(request.categoryName())
                .build();

        when(productMapper.toDomain(request)).thenReturn(domainProduct);
        when(productMapper.toResponseDto(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            return new ProductResponseDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getCategoryName());
        });

        ProductResponseDto created = productService.createProduct(request);

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Starship Carrier");
        assertThat(productService.getAllProducts()).hasSize(1);
        verify(productMapper).toDomain(request);
    }

    @Test
    @DisplayName("Should return product when found by existing ID")
    void getProductById_ExistingId_ReturnsProduct() {
        ProductRequestDto request = new ProductRequestDto("Astro Bowl", "Anti-spill", new BigDecimal("15.00"), "Bowls");
        when(productMapper.toDomain(request)).thenReturn(Product.builder().name("Astro Bowl").build());
        when(productMapper.toResponseDto(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            return new ProductResponseDto(p.getId(), p.getName(), null, null, null);
        });

        ProductResponseDto created = productService.createProduct(request);
        ProductResponseDto found = productService.getProductById(created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.name()).isEqualTo("Astro Bowl");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product ID does not exist")
    void getProductById_NonExistentId_ThrowsException() {
        UUID randomId = UUID.randomUUID();

        assertThatThrownBy(() -> productService.getProductById(randomId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(randomId.toString());
    }

    @Test
    @DisplayName("Should update existing product fields")
    void updateProduct_ExistingId_UpdatesAndReturnsDto() {
        ProductRequestDto initial = new ProductRequestDto("Orbit Toy", "Desc 1", new BigDecimal("10.00"), "Toys");
        ProductRequestDto updatedReq = new ProductRequestDto("Supernova Toy", "Desc 2", new BigDecimal("12.50"), "Toys");

        when(productMapper.toDomain(any())).thenAnswer(inv -> {
            ProductRequestDto r = inv.getArgument(0);
            return Product.builder().name(r.name()).description(r.description()).price(r.price()).categoryName(r.categoryName()).build();
        });
        when(productMapper.toResponseDto(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            return new ProductResponseDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getCategoryName());
        });

        ProductResponseDto created = productService.createProduct(initial);
        ProductResponseDto updated = productService.updateProduct(created.id(), updatedReq);

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.name()).isEqualTo("Supernova Toy");
        assertThat(updated.price()).isEqualByComparingTo(new BigDecimal("12.50"));
    }

    @Test
    @DisplayName("Should delete existing product and throw on subsequent lookup")
    void deleteProduct_ExistingId_RemovesProduct() {
        ProductRequestDto request = new ProductRequestDto("Cosmo Mat", "Soft mat", new BigDecimal("25.00"), "Bedding");
        when(productMapper.toDomain(request)).thenReturn(Product.builder().name("Cosmo Mat").build());
        when(productMapper.toResponseDto(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            return new ProductResponseDto(p.getId(), p.getName(), null, null, null);
        });

        ProductResponseDto created = productService.createProduct(request);
        productService.deleteProduct(created.id());

        List<ProductResponseDto> all = productService.getAllProducts();
        assertThat(all).isEmpty();
        assertThatThrownBy(() -> productService.deleteProduct(created.id()))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
