package com.cosmocats.marketplace.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CosmicWordValidatorTest {

    private CosmicWordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CosmicWordValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Cosmic Laser Pointer",
            "GALAXY Scratcher",
            "Nebula Catnip",
            "Starlight Collar",
            "Astro Cat Bed",
            "Orbit Ball"
    })
    @DisplayName("Should return true when product name contains a cosmic word")
    void isValid_ValidCosmicNames_ReturnsTrue(String productName) {
        assertThat(validator.isValid(productName, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Ordinary Cat Bowl",
            "Simple Scratching Post",
            "Regular Collar"
    })
    @DisplayName("Should return false when product name has no cosmic terms")
    void isValid_NonCosmicNames_ReturnsFalse(String productName) {
        assertThat(validator.isValid(productName, null)).isFalse();
    }

    @Test
    @DisplayName("Should return true for null or blank values so @NotBlank handles them")
    void isValid_NullOrBlank_ReturnsTrue() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }
}
