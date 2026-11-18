package com.cosmocats.marketplace.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=true")
@EmbeddedKafka(partitions = 1, topics = {"cosmocats.cart.events", "cosmocats.cart.events.DLT"})
@DirtiesContext
class CartKafkaIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should publish cart item event to Kafka and asynchronously persist into user cart")
    void addItemToCart_EndToEndKafkaFlow_UpdatesUserCart() throws Exception {
        UUID userId = UUID.randomUUID();
        String seededProductId = "33333333-3333-3333-3333-333333333333";

        String requestBody = """
                {
                  "userId": "%s",
                  "productId": "%s",
                  "quantity": 3
                }
                """.formatted(userId, seededProductId);

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("QUEUED"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        // Ожидаем асинхронную обработку сообщения консьюмером из EmbeddedKafka
        long deadline = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis();
        int itemsCount = 0;
        while (System.currentTimeMillis() < deadline) {
            MvcResult getResult = mockMvc.perform(get("/api/v1/cart/{userId}", userId))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = getResult.getResponse().getContentAsString();
            itemsCount = ((Number) JsonPath.read(json, "$.items.length()")).intValue();
            if (itemsCount > 0) {
                assertThat((String) JsonPath.read(json, "$.items[0].productName")).isEqualTo("Cosmic Laser Pointer");
                assertThat(((Number) JsonPath.read(json, "$.items[0].quantity")).intValue()).isEqualTo(3);
                assertThat(((Number) JsonPath.read(json, "$.totalPrice")).doubleValue()).isEqualTo(59.97);
                return;
            }
            Thread.sleep(200);
        }

        throw new AssertionError("Timed out waiting for Kafka consumer to persist cart item");
    }
}
