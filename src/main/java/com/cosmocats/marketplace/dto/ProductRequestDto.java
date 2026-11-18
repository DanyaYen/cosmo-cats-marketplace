package com.cosmocats.marketplace.dto;

import com.cosmocats.marketplace.validation.CosmicWordCheck;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank(message = "Product name must not be blank")
        @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
        @CosmicWordCheck
        String name,

        @NotBlank(message = "Description must not be blank")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Category name is required")
        String categoryName
) {}
