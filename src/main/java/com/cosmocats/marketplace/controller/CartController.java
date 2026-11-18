package com.cosmocats.marketplace.controller;

import com.cosmocats.marketplace.dto.AddToCartRequestDto;
import com.cosmocats.marketplace.dto.CartEventAcceptedDto;
import com.cosmocats.marketplace.dto.CartResponseDto;
import com.cosmocats.marketplace.kafka.CartCommandService;
import com.cosmocats.marketplace.service.CartQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart API (Kafka)", description = "Asynchronous cart operations via Apache Kafka")
public class CartController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Queue an item addition to user's cart via Kafka")
    public CartEventAcceptedDto addItemToCart(@Valid @RequestBody AddToCartRequestDto requestDto) {
        return cartCommandService.publishAddItemEvent(requestDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get current user's cart state")
    public CartResponseDto getCart(@PathVariable UUID userId) {
        return cartQueryService.getCartByUserId(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Clear user's cart")
    public void clearCart(@PathVariable UUID userId) {
        cartQueryService.clearCart(userId);
    }
}
