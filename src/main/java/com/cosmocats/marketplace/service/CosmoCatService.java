package com.cosmocats.marketplace.service;

import com.cosmocats.marketplace.aspect.FeatureToggle;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CosmoCatService {

    @FeatureToggle("cosmoCats")
    public List<String> getCosmoCats() {
        return List.of("Captain Whiskers", "Nebula Purr", "Astro Mittens");
    }

    @FeatureToggle("kittyProducts")
    public List<String> getKittyProducts() {
        return List.of("Mini Star Bell", "Kitten Moon Milk");
    }
}
