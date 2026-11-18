package com.cosmocats.marketplace.kafka;

import com.cosmocats.marketplace.entity.CartEntity;
import com.cosmocats.marketplace.entity.CartItemEntity;
import com.cosmocats.marketplace.entity.ProcessedEventEntity;
import com.cosmocats.marketplace.event.CartItemAddedEvent;
import com.cosmocats.marketplace.repository.CartRepository;
import com.cosmocats.marketplace.repository.ProcessedEventRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartEventConsumer {

    private final CartRepository cartRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.cart-events}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onCartItemAdded(String rawPayload) {
        CartItemAddedEvent event = objectMapper.readValue(rawPayload, CartItemAddedEvent.class);

        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate CartItemAddedEvent detected and skipped [eventId={}]", event.eventId());
            return;
        }

        CartEntity cart = cartRepository.findByUserId(event.userId())
                .orElseGet(() -> CartEntity.builder()
                        .userId(event.userId())
                        .build());

        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(event.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + event.quantity());
            item.setUnitPrice(event.unitPrice());
        } else {
            CartItemEntity newItem = CartItemEntity.builder()
                    .cart(cart)
                    .productId(event.productId())
                    .productName(event.productName())
                    .quantity(event.quantity())
                    .unitPrice(event.unitPrice())
                    .build();
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        processedEventRepository.save(new ProcessedEventEntity(event.eventId(), Instant.now()));
        log.info("Processed CartItemAddedEvent [eventId={}] into cart for user [{}]",
                event.eventId(), event.userId());
    }
}
