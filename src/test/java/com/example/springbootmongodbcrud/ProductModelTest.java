package com.example.springbootmongodbcrud;

import com.example.springbootmongodbcrud.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductModelTest {

    @Test
    void testDefaultConstructor() {
        Product product = new Product();
        
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getCategory());
        assertNull(product.getStockQuantity());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("99.99");
        String category = "Electronics";
        Integer stockQuantity = 10;
        
        Product product = new Product(name, description, price, category, stockQuantity);
        
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertEquals(stockQuantity, product.getStockQuantity());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    void testGettersAndSetters() {
        Product product = new Product();
        
        String id = "123";
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("99.99");
        String category = "Electronics";
        Integer stockQuantity = 10;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setStockQuantity(stockQuantity);
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(updatedAt);
        
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertEquals(stockQuantity, product.getStockQuantity());
        assertEquals(createdAt, product.getCreatedAt());
        assertEquals(updatedAt, product.getUpdatedAt());
    }

    @Test
    void testToString() {
        Product product = new Product("Test Product", "Test Description", new BigDecimal("99.99"), "Electronics", 10);
        product.setId("123");
        
        String toString = product.toString();
        
        assertTrue(toString.contains("Product{"));
        assertTrue(toString.contains("id='123'"));
        assertTrue(toString.contains("name='Test Product'"));
        assertTrue(toString.contains("description='Test Description'"));
        assertTrue(toString.contains("price=99.99"));
        assertTrue(toString.contains("category='Electronics'"));
        assertTrue(toString.contains("stockQuantity=10"));
        assertTrue(toString.contains("createdAt="));
        assertTrue(toString.contains("updatedAt="));
    }

    @Test
    void testEqualsAndHashCode() {
        Product product1 = new Product("Test Product", "Description", new BigDecimal("99.99"), "Electronics", 10);
        Product product2 = new Product("Test Product", "Description", new BigDecimal("99.99"), "Electronics", 10);
        Product product3 = new Product("Different Product", "Description", new BigDecimal("99.99"), "Electronics", 10);
        
        // Test equals
        assertEquals(product1, product1); // Same object
        assertNotEquals(product1, product3); // Different objects with different names
        
        // Test hashCode
        assertEquals(product1.hashCode(), product1.hashCode());
        assertNotEquals(product1.hashCode(), product3.hashCode());
    }

    @Test
    void testPriceValidation() {
        Product product = new Product();
        
        // Test with positive price
        BigDecimal positivePrice = new BigDecimal("99.99");
        product.setPrice(positivePrice);
        assertEquals(positivePrice, product.getPrice());
        
        // Test with zero price
        BigDecimal zeroPrice = BigDecimal.ZERO;
        product.setPrice(zeroPrice);
        assertEquals(zeroPrice, product.getPrice());
        
        // Test with negative price (should still work as validation is handled by annotations)
        BigDecimal negativePrice = new BigDecimal("-10.00");
        product.setPrice(negativePrice);
        assertEquals(negativePrice, product.getPrice());
    }

    @Test
    void testStockQuantityValidation() {
        Product product = new Product();
        
        // Test with positive quantity
        product.setStockQuantity(10);
        assertEquals(10, product.getStockQuantity());
        
        // Test with zero quantity
        product.setStockQuantity(0);
        assertEquals(0, product.getStockQuantity());
        
        // Test with negative quantity (should still work as validation is handled by annotations)
        product.setStockQuantity(-5);
        assertEquals(-5, product.getStockQuantity());
    }

    @Test
    void testTimestampInitialization() {
        Product product = new Product();
        
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
        
        // Timestamps should be close to current time
        LocalDateTime now = LocalDateTime.now();
        assertTrue(product.getCreatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(product.getCreatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(product.getUpdatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(product.getUpdatedAt().isAfter(now.minusSeconds(1)));
    }

    @Test
    void testParameterizedConstructorTimestampInitialization() {
        Product product = new Product("Test", "Description", new BigDecimal("10.00"), "Category", 5);
        
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
        
        // Timestamps should be close to current time
        LocalDateTime now = LocalDateTime.now();
        assertTrue(product.getCreatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(product.getCreatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(product.getUpdatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(product.getUpdatedAt().isAfter(now.minusSeconds(1)));
    }
} 