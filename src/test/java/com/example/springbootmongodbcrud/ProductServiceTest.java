package com.example.springbootmongodbcrud;

import com.example.springbootmongodbcrud.model.Product;
import com.example.springbootmongodbcrud.repository.ProductRepository;
import com.example.springbootmongodbcrud.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product("Test Product", "Description", new BigDecimal("10.00"), "Category", 5);
        product.setId("1");
    }

    @Test
    void testCreateProduct() {
        when(productRepository.existsByName(product.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product created = productService.createProduct(product);
        assertNotNull(created);
        assertEquals(product.getName(), created.getName());
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        List<Product> products = productService.getAllProducts();
        assertEquals(1, products.size());
    }

    @Test
    void testGetProductById() {
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        Optional<Product> found = productService.getProductById("1");
        assertTrue(found.isPresent());
        assertEquals(product.getName(), found.get().getName());
    }

    @Test
    void testUpdateProduct() {
        Product updated = new Product("Updated", "Desc", new BigDecimal("20.00"), "Category", 10);
        updated.setId("1");
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.existsByName(updated.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(updated);
        Product result = productService.updateProduct("1", updated);
        assertEquals("Updated", result.getName());
        assertEquals(new BigDecimal("20.00"), result.getPrice());
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.existsById("1")).thenReturn(true);
        doNothing().when(productRepository).deleteById("1");
        assertDoesNotThrow(() -> productService.deleteProduct("1"));
        verify(productRepository, times(1)).deleteById("1");
    }
} 