package com.cosmocats.marketplace.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cosmocats.marketplace.entity.CategoryEntity;
import com.cosmocats.marketplace.entity.ProductEntity;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Liquibase should seed initial categories and products")
    void liquibaseSeed_InitialDataLoaded() {
        List<ProductEntity> seededProducts = productRepository.findAll();

        assertThat(seededProducts).hasSizeGreaterThanOrEqualTo(2);
        assertThat(categoryRepository.findByNameIgnoreCase("Toys")).isPresent();
        assertThat(categoryRepository.findByNameIgnoreCase("Treats")).isPresent();
    }

    @Test
    @DisplayName("Should save product with category relation and query by category name")
    void saveAndFindByCategoryName_PersistsRelation() {
        CategoryEntity gear = categoryRepository.save(
                CategoryEntity.builder()
                        .name("Space Gear")
                        .description("Helmets and suits")
                        .build()
        );

        ProductEntity helmet = productRepository.save(
                ProductEntity.builder()
                        .name("Orbit Visor Helmet")
                        .description("Anti-asteroid feline helmet")
                        .price(new BigDecimal("199.99"))
                        .category(gear)
                        .build()
        );

        List<ProductEntity> found = productRepository.findByCategoryNameIgnoreCase("space gear");

        assertThat(helmet.getId()).isNotNull();
        assertThat(helmet.getCreatedAt()).isNotNull();
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Orbit Visor Helmet");
    }
}
