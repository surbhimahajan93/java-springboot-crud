package com.example.springbootmongodbcrud;

import com.example.springbootmongodbcrud.model.Product;
import com.example.springbootmongodbcrud.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        
        product1 = new Product("Laptop", "High-performance laptop", new BigDecimal("999.99"), "Electronics", 10);
        product2 = new Product("Mouse", "Wireless mouse", new BigDecimal("29.99"), "Electronics", 50);
        product3 = new Product("Book", "Programming book", new BigDecimal("49.99"), "Books", 25);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
    }

    @Test
    void testFindByName() {
        Optional<Product> found = productRepository.findByName("Laptop");
        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().getName());
        assertEquals("Electronics", found.get().getCategory());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<Product> found = productRepository.findByName("NonExistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByCategory() {
        List<Product> electronics = productRepository.findByCategory("Electronics");
        assertEquals(2, electronics.size());
        assertTrue(electronics.stream().allMatch(p -> "Electronics".equals(p.getCategory())));
    }

    @Test
    void testFindByPriceLessThan() {
        List<Product> cheapProducts = productRepository.findByPriceLessThan(new BigDecimal("50.00"));
        assertEquals(2, cheapProducts.size());
        assertTrue(cheapProducts.stream().allMatch(p -> p.getPrice().compareTo(new BigDecimal("50.00")) < 0));
    }

    @Test
    void testFindByPriceGreaterThan() {
        List<Product> expensiveProducts = productRepository.findByPriceGreaterThan(new BigDecimal("500.00"));
        assertEquals(1, expensiveProducts.size());
        assertEquals("Laptop", expensiveProducts.get(0).getName());
    }

    @Test
    void testFindByStockQuantityLessThan() {
        List<Product> lowStock = productRepository.findByStockQuantityLessThan(30);
        assertEquals(2, lowStock.size());
        assertTrue(lowStock.stream().allMatch(p -> p.getStockQuantity() < 30));
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        List<Product> products = productRepository.findByNameContainingIgnoreCase("laptop");
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }

    @Test
    void testFindByPriceBetween() {
        List<Product> products = productRepository.findByPriceBetween(new BigDecimal("20.00"), new BigDecimal("100.00"));
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(p -> 
            p.getPrice().compareTo(new BigDecimal("20.00")) >= 0 && 
            p.getPrice().compareTo(new BigDecimal("100.00")) <= 0));
    }

    @Test
    void testFindByCategoryAndPriceBetween() {
        List<Product> products = productRepository.findByCategoryAndPriceBetween(
            "Electronics", new BigDecimal("20.00"), new BigDecimal("100.00"));
        assertEquals(1, products.size());
        assertEquals("Mouse", products.get(0).getName());
    }

    @Test
    void testExistsByName() {
        assertTrue(productRepository.existsByName("Laptop"));
        assertFalse(productRepository.existsByName("NonExistent"));
    }

    @Test
    void testSaveAndFindById() {
        Product newProduct = new Product("New Product", "Description", new BigDecimal("100.00"), "Test", 5);
        Product saved = productRepository.save(newProduct);
        
        assertNotNull(saved.getId());
        
        Optional<Product> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("New Product", found.get().getName());
    }

    @Test
    void testUpdateProduct() {
        Product toUpdate = productRepository.findByName("Laptop").get();
        toUpdate.setPrice(new BigDecimal("899.99"));
        toUpdate.setStockQuantity(15);
        
        Product updated = productRepository.save(toUpdate);
        assertEquals(new BigDecimal("899.99"), updated.getPrice());
        assertEquals(15, updated.getStockQuantity());
        
        // Verify the update persisted
        Product found = productRepository.findById(updated.getId()).get();
        assertEquals(new BigDecimal("899.99"), found.getPrice());
        assertEquals(15, found.getStockQuantity());
    }

    @Test
    void testDeleteProduct() {
        Product toDelete = productRepository.findByName("Laptop").get();
        String id = toDelete.getId();
        
        productRepository.deleteById(id);
        
        assertFalse(productRepository.findById(id).isPresent());
        assertFalse(productRepository.existsByName("Laptop"));
    }

    @Test
    void testFindAll() {
        List<Product> allProducts = productRepository.findAll();
        assertEquals(3, allProducts.size());
        
        List<String> names = allProducts.stream().map(Product::getName).toList();
        assertTrue(names.contains("Laptop"));
        assertTrue(names.contains("Mouse"));
        assertTrue(names.contains("Book"));
    }
} 