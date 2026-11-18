package com.cosmocats.marketplace.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    @DisplayName("Should accurately calculate total cart price using BigDecimal")
    void getTotalPrice_MultipleItems_CalculatesExactSum() {
        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .items(List.of(
                        CartItem.builder()
                                .productId(UUID.randomUUID())
                                .productName("Cosmic Laser")
                                .quantity(2)
                                .unitPrice(new BigDecimal("19.99"))
                                .build(),
                        CartItem.builder()
                                .productId(UUID.randomUUID())
                                .productName("Nebula Catnip")
                                .quantity(3)
                                .unitPrice(new BigDecimal("9.50"))
                                .build()
                ))
                .build();

        assertThat(cart.getTotalPrice()).isEqualByComparingTo(new BigDecimal("68.48"));
    }

    @Test
    @DisplayName("Should return zero when cart is empty")
    void getTotalPrice_EmptyCart_ReturnsZero() {
        Cart cart = Cart.builder().build();
        assertThat(cart.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
