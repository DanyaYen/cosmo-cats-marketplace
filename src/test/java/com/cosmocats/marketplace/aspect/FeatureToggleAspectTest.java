package com.cosmocats.marketplace.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.cosmocats.marketplace.config.FeatureToggleConfig.FeatureProperty;
import com.cosmocats.marketplace.exception.FeatureNotAvailableException;
import com.cosmocats.marketplace.service.CosmoCatService;
import com.cosmocats.marketplace.service.FeatureToggleService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class FeatureToggleAspectTest {

    private CosmoCatService createProxiedService(boolean cosmoCatsEnabled, boolean kittyProductsEnabled) {
        FeatureProperty cosmoProp = new FeatureProperty();
        cosmoProp.setEnabled(cosmoCatsEnabled);

        FeatureProperty kittyProp = new FeatureProperty();
        kittyProp.setEnabled(kittyProductsEnabled);

        FeatureToggleService toggleService = new FeatureToggleService(Map.of(
                "cosmoCats", cosmoProp,
                "kittyProducts", kittyProp
        ));
        FeatureToggleAspect aspect = new FeatureToggleAspect(toggleService);

        AspectJProxyFactory factory = new AspectJProxyFactory(new CosmoCatService());
        factory.addAspect(aspect);
        return factory.getProxy();
    }

    @Test
    @DisplayName("Should execute getCosmoCats() when feature.cosmoCats.enabled is true")
    void getCosmoCats_WhenFlagEnabled_ExecutesSuccessfully() {
        CosmoCatService proxiedService = createProxiedService(true, false);

        List<String> cats = proxiedService.getCosmoCats();

        assertThat(cats).containsExactly("Captain Whiskers", "Nebula Purr", "Astro Mittens");
    }

    @Test
    @DisplayName("Should throw FeatureNotAvailableException when feature.cosmoCats.enabled is false")
    void getCosmoCats_WhenFlagDisabled_ThrowsException() {
        CosmoCatService proxiedService = createProxiedService(false, false);

        assertThatThrownBy(proxiedService::getCosmoCats)
                .isInstanceOf(FeatureNotAvailableException.class)
                .hasMessageContaining("cosmoCats");
    }

    @Test
    @DisplayName("Should throw FeatureNotAvailableException when feature.kittyProducts.enabled is false")
    void getKittyProducts_WhenFlagDisabled_ThrowsException() {
        CosmoCatService proxiedService = createProxiedService(true, false);

        assertThatThrownBy(proxiedService::getKittyProducts)
                .isInstanceOf(FeatureNotAvailableException.class)
                .hasMessageContaining("kittyProducts");
    }
}
