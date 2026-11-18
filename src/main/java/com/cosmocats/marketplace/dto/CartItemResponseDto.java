package com.cosmocats.marketplace.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponseDto(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
