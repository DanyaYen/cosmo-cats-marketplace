package com.cosmocats.marketplace.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.dto.ProductRequestDto;
import com.cosmocats.marketplace.dto.ProductResponseDto;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapperImpl();

    @Test
    @DisplayName("Should map ProductRequestDto to Product domain entity ignoring id")
    void toDomain_ValidRequestDto_MapsFieldsCorrectly() {
        ProductRequestDto dto = new ProductRequestDto(
                "Comet Tail Toy",
                "Glowing feather wand",
                new BigDecimal("14.99"),
                "Toys"
        );

        Product domain = mapper.toDomain(dto);

        assertThat(domain.getId()).isNull();
        assertThat(domain.getName()).isEqualTo("Comet Tail Toy");
        assertThat(domain.getDescription()).isEqualTo("Glowing feather wand");
        assertThat(domain.getPrice()).isEqualByComparingTo(new BigDecimal("14.99"));
        assertThat(domain.getCategoryName()).isEqualTo("Toys");
    }

    @Test
    @DisplayName("Should map Product domain entity to ProductResponseDto")
    void toResponseDto_ValidProduct_MapsAllFields() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder()
                .id(id)
                .name("Lunar Litter Box")
                .description("Self-cleaning gravity pod")
                .price(new BigDecimal("129.00"))
                .categoryName("Accessories")
                .build();

        ProductResponseDto responseDto = mapper.toResponseDto(product);

        assertThat(responseDto.id()).isEqualTo(id);
        assertThat(responseDto.name()).isEqualTo("Lunar Litter Box");
        assertThat(responseDto.price()).isEqualByComparingTo(new BigDecimal("129.00"));
        assertThat(responseDto.categoryName()).isEqualTo("Accessories");
    }
}
