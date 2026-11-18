package com.cosmocats.marketplace.integration;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class MarketplaceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Full CRUD lifecycle through real Spring context")
    void fullProductLifecycle_CreateReadDelete_WorksEndToEnd() throws Exception {
        String createPayload = """
                {
                  "name": "Planetarium Cat Tower",
                  "description": "Multi-tier observatory for space cats",
                  "price": 149.99,
                  "categoryName": "Furniture"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Planetarium Cat Tower"))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String createdId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Planetarium Cat Tower")));

        mockMvc.perform(delete("/api/v1/products/{id}", createdId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", createdId))
                .andExpect(status().isNotFound());
    }
}
