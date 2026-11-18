package com.cosmocats.marketplace.controller;

import com.cosmocats.marketplace.service.CosmoCatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cosmo-cats")
@RequiredArgsConstructor
@Tag(name = "Cosmo Cats API", description = "Feature-toggled endpoints controlled via Spring AOP")
public class CosmoCatController {

    private final CosmoCatService cosmoCatService;

    @GetMapping
    @Operation(summary = "Get active cosmo cats (controlled by feature.cosmoCats.enabled)")
    public List<String> getCosmoCats() {
        return cosmoCatService.getCosmoCats();
    }

    @GetMapping("/kitty-products")
    @Operation(summary = "Get kitty products (controlled by feature.kittyProducts.enabled)")
    public List<String> getKittyProducts() {
        return cosmoCatService.getKittyProducts();
    }
}
