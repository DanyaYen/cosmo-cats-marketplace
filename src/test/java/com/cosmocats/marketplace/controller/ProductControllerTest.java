package com.cosmocats.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cosmocats.marketplace.dto.ProductResponseDto;
import com.cosmocats.marketplace.exception.GlobalExceptionHandler;
import com.cosmocats.marketplace.exception.ProductNotFoundException;
import com.cosmocats.marketplace.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/products should return 200 OK with list of products")
    void getAllProducts_ReturnsOkWithList() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getAllProducts()).thenReturn(List.of(
                new ProductResponseDto(id, "Cosmic Laser", "Red dot", new BigDecimal("19.99"), "Toys")
        ));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value("Cosmic Laser"));
    }

    @Test
    @DisplayName("POST /api/v1/products with valid cosmic payload should return 201 Created")
    void createProduct_ValidPayload_Returns201Created() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.createProduct(any())).thenReturn(
                new ProductResponseDto(id, "Galaxy Scratcher", "Orbital post", new BigDecimal("45.99"), "Furniture")
        );

        String validJson = """
                {
                  "name": "Galaxy Scratcher",
                  "description": "Orbital post",
                  "price": 45.99,
                  "categoryName": "Furniture"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Galaxy Scratcher"));
    }

    @Test
    @DisplayName("POST /api/v1/products without cosmic word should return 400 RFC 9457 ProblemDetail")
    void createProduct_InvalidCosmicNameAndNegativePrice_Returns400ProblemDetail() throws Exception {
        String invalidJson = """
                {
                  "name": "Regular Bowl",
                  "description": "Plain bowl",
                  "price": -5.00,
                  "categoryName": "Bowls"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.invalidParams").isArray())
                .andExpect(jsonPath("$.invalidParams.length()").value(2));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} when missing should return 404 RFC 9457 ProblemDetail")
    void deleteProduct_NotFound_Returns404ProblemDetail() throws Exception {
        UUID missingId = UUID.randomUUID();
        doThrow(new ProductNotFoundException(missingId)).when(productService).deleteProduct(missingId);

        mockMvc.perform(delete("/api/v1/products/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Product Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
