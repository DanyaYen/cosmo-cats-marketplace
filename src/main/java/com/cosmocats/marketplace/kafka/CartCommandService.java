package com.cosmocats.marketplace.kafka;

import com.cosmocats.marketplace.dto.AddToCartRequestDto;
import com.cosmocats.marketplace.dto.CartEventAcceptedDto;
import com.cosmocats.marketplace.entity.ProductEntity;
import com.cosmocats.marketplace.event.CartItemAddedEvent;
import com.cosmocats.marketplace.exception.ProductNotFoundException;
import com.cosmocats.marketplace.repository.ProductRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCommandService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.cart-events}")
    private String cartEventsTopic;

    @Transactional(readOnly = true)
    public CartEventAcceptedDto publishAddItemEvent(AddToCartRequestDto request) {
        ProductEntity product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        UUID eventId = UUID.randomUUID();
        Instant now = Instant.now();

        CartItemAddedEvent event = new CartItemAddedEvent(
                eventId,
                request.userId(),
                product.getId(),
                product.getName(),
                request.quantity(),
                product.getPrice(),
                now
        );

        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send(cartEventsTopic, request.userId().toString(), payload);
        log.info("Published CartItemAddedEvent [eventId={}] for user [{}] to topic [{}]",
                eventId, request.userId(), cartEventsTopic);

        return new CartEventAcceptedDto(
                eventId,
                request.userId(),
                product.getId(),
                request.quantity(),
                "QUEUED",
                now
        );
    }
}
