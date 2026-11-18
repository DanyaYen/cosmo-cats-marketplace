package com.cosmocats.marketplace.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class FeatureToggleConfig {

    @Data
    public static class FeatureProperty {
        private boolean enabled;
    }

    @Bean
    @ConfigurationProperties(prefix = "feature")
    public Map<String, FeatureProperty> featureToggles() {
        return new HashMap<>();
    }
}
