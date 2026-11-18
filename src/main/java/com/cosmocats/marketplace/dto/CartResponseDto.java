package com.cosmocats.marketplace.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponseDto(
        UUID cartId,
        UUID userId,
        List<CartItemResponseDto> items,
        BigDecimal totalPrice
) {}
