package com.example.springbootmongodbcrud;

import com.example.springbootmongodbcrud.model.Product;
import com.example.springbootmongodbcrud.repository.ProductRepository;
import com.example.springbootmongodbcrud.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        productRepository.deleteAll();
    }

    @Test
    void testCompleteProductLifecycle() throws Exception {
        // Create a product
        Product product = new Product("Integration Test Product", "Test Description", new BigDecimal("199.99"), "Electronics", 15);
        
        String productJson = objectMapper.writeValueAsString(product);
        
        // Test POST - Create product
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Product"))
                .andExpect(jsonPath("$.price").value(199.99))
                .andReturn().getResponse().getContentAsString();
        
        Product createdProduct = objectMapper.readValue(response, Product.class);
        assertNotNull(createdProduct.getId());
        
        // Test GET - Retrieve product by ID
        mockMvc.perform(get("/api/products/" + createdProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Product"));
        
        // Test PUT - Update product
        Product updatedProduct = new Product("Updated Integration Test Product", "Updated Description", new BigDecimal("299.99"), "Electronics", 20);
        updatedProduct.setId(createdProduct.getId());
        
        mockMvc.perform(put("/api/products/" + createdProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Integration Test Product"))
                .andExpect(jsonPath("$.price").value(299.99));
        
        // Test PATCH - Update stock quantity
        mockMvc.perform(patch("/api/products/" + createdProduct.getId() + "/stock")
                .param("quantity", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(25));
        
        // Test DELETE - Delete product
        mockMvc.perform(delete("/api/products/" + createdProduct.getId()))
                .andExpect(status().isNoContent());
        
        // Verify product is deleted
        mockMvc.perform(get("/api/products/" + createdProduct.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testProductSearchAndFiltering() throws Exception {
        // Create multiple products
        Product product1 = new Product("Laptop", "High-performance laptop", new BigDecimal("999.99"), "Electronics", 10);
        Product product2 = new Product("Mouse", "Wireless mouse", new BigDecimal("29.99"), "Electronics", 50);
        Product product3 = new Product("Book", "Programming book", new BigDecimal("49.99"), "Books", 25);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        
        // Test search by name
        mockMvc.perform(get("/api/products/search")
                .param("name", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
        
        // Test filter by category
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        
        // Test filter by price range
        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "20")
                .param("maxPrice", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        
        // Test filter by low stock
        mockMvc.perform(get("/api/products/low-stock")
                .param("quantity", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        
        // Test combined filter
        mockMvc.perform(get("/api/products/category/Electronics/price-range")
                .param("minPrice", "20")
                .param("maxPrice", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Mouse"));
    }

    @Test
    void testErrorHandling() throws Exception {
        // Test getting non-existent product
        mockMvc.perform(get("/api/products/nonexistent-id"))
                .andExpect(status().isNotFound());
        
        // Test updating non-existent product
        Product product = new Product("Test", "Description", new BigDecimal("10.00"), "Category", 5);
        mockMvc.perform(put("/api/products/nonexistent-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
        
        // Test deleting non-existent product
        mockMvc.perform(delete("/api/products/nonexistent-id"))
                .andExpect(status().isNotFound());
        
        // Test updating stock of non-existent product
        mockMvc.perform(patch("/api/products/nonexistent-id/stock")
                .param("quantity", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDuplicateProductName() throws Exception {
        // Create first product
        Product product1 = new Product("Duplicate Name", "Description 1", new BigDecimal("10.00"), "Category", 5);
        productRepository.save(product1);
        
        // Try to create second product with same name
        Product product2 = new Product("Duplicate Name", "Description 2", new BigDecimal("20.00"), "Category", 10);
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testServiceLayerIntegration() {
        // Test service layer methods
        Product product = new Product("Service Test", "Description", new BigDecimal("100.00"), "Test", 10);
        
        // Create
        Product created = productService.createProduct(product);
        assertNotNull(created.getId());
        assertEquals("Service Test", created.getName());
        
        // Read
        List<Product> allProducts = productService.getAllProducts();
        assertFalse(allProducts.isEmpty());
        assertTrue(allProducts.stream().anyMatch(p -> "Service Test".equals(p.getName())));
        
        // Update
        Product updateData = new Product("Updated Service Test", "Updated Description", new BigDecimal("150.00"), "Test", 15);
        Product updated = productService.updateProduct(created.getId(), updateData);
        assertEquals("Updated Service Test", updated.getName());
        assertEquals(new BigDecimal("150.00"), updated.getPrice());
        
        // Delete
        productService.deleteProduct(created.getId());
        assertTrue(productService.getProductById(created.getId()).isEmpty());
    }
} 