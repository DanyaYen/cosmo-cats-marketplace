package com.cosmocats.marketplace.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;

public class CosmicWordValidator implements ConstraintValidator<CosmicWordCheck, String> {

    private static final List<String> COSMIC_WORDS = List.of(
            "star", "galaxy", "cosmo", "cosmic", "comet", "nebula", "orbit", "astro", "planet", "lunar"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String lowerCaseValue = value.toLowerCase(Locale.ROOT);
        return COSMIC_WORDS.stream().anyMatch(lowerCaseValue::contains);
    }
}
