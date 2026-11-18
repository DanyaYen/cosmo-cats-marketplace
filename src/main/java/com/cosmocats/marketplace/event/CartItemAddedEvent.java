package com.cosmocats.marketplace.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartItemAddedEvent(
        UUID eventId,
        UUID userId,
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        Instant occurredAt
) {}
