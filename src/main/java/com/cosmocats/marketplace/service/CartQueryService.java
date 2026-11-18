package com.cosmocats.marketplace.service;

import com.cosmocats.marketplace.dto.CartItemResponseDto;
import com.cosmocats.marketplace.dto.CartResponseDto;
import com.cosmocats.marketplace.entity.CartEntity;
import com.cosmocats.marketplace.repository.CartRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryService {

    private final CartRepository cartRepository;

    public CartResponseDto getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .map(this::toResponseDto)
                .orElseGet(() -> new CartResponseDto(null, userId, List.of(), BigDecimal.ZERO));
    }

    @Transactional
    public void clearCart(UUID userId) {
        cartRepository.findByUserId(userId).ifPresent(cartRepository::delete);
    }

    private CartResponseDto toResponseDto(CartEntity cart) {
        List<CartItemResponseDto> items = cart.getItems().stream()
                .map(item -> {
                    BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResponseDto(
                            item.getProductId(),
                            item.getProductName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            subtotal
                    );
                })
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponseDto::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDto(cart.getId(), cart.getUserId(), items, total);
    }
}
