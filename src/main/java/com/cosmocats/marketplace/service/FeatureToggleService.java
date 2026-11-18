package com.cosmocats.marketplace.service;

import com.cosmocats.marketplace.config.FeatureToggleConfig.FeatureProperty;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    private final Map<String, FeatureProperty> featureToggles;

    public FeatureToggleService(@Qualifier("featureToggles") Map<String, FeatureProperty> featureToggles) {
        this.featureToggles = featureToggles;
    }

    public boolean isFeatureEnabled(String featureName) {
        FeatureProperty property = featureToggles.get(featureName);
        return property != null && property.isEnabled();
    }
}
