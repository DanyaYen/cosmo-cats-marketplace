package com.cosmocats.marketplace.dto;

import java.time.Instant;
import java.util.UUID;

public record CartEventAcceptedDto(
        UUID eventId,
        UUID userId,
        UUID productId,
        int quantity,
        String status,
        Instant acceptedAt
) {}
