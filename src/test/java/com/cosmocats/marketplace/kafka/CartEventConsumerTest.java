package com.cosmocats.marketplace.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmocats.marketplace.entity.CartEntity;
import com.cosmocats.marketplace.entity.ProcessedEventEntity;
import com.cosmocats.marketplace.event.CartItemAddedEvent;
import com.cosmocats.marketplace.repository.CartRepository;
import com.cosmocats.marketplace.repository.ProcessedEventRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CartEventConsumerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CartEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new CartEventConsumer(cartRepository, processedEventRepository, objectMapper);
    }

    @Test
    @DisplayName("Should create new cart and persist item when valid event arrives")
    void onCartItemAdded_NewUserCart_CreatesAndSavesItem() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CartItemAddedEvent event = new CartItemAddedEvent(
                eventId, userId, productId, "Cosmic Laser Pointer", 2, new BigDecimal("19.99"), Instant.now()
        );
        String payload = objectMapper.writeValueAsString(event);

        when(processedEventRepository.existsById(eventId)).thenReturn(false);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        consumer.onCartItemAdded(payload);

        ArgumentCaptor<CartEntity> cartCaptor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(cartCaptor.capture());
        CartEntity savedCart = cartCaptor.getValue();

        assertThat(savedCart.getUserId()).isEqualTo(userId);
        assertThat(savedCart.getItems()).hasSize(1);
        assertThat(savedCart.getItems().getFirst().getQuantity()).isEqualTo(2);
        verify(processedEventRepository).save(any(ProcessedEventEntity.class));
    }

    @Test
    @DisplayName("Should ignore duplicate eventId to ensure idempotency")
    void onCartItemAdded_DuplicateEventId_SkipsProcessing() {
        UUID eventId = UUID.randomUUID();
        CartItemAddedEvent event = new CartItemAddedEvent(
                eventId, UUID.randomUUID(), UUID.randomUUID(), "Nebula Catnip", 1, new BigDecimal("9.50"), Instant.now()
        );
        String payload = objectMapper.writeValueAsString(event);

        when(processedEventRepository.existsById(eventId)).thenReturn(true);

        consumer.onCartItemAdded(payload);

        verify(cartRepository, never()).save(any());
        verify(processedEventRepository, never()).save(any());
    }
}
