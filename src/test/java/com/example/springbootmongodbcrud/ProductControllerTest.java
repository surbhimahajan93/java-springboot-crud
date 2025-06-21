package com.example.springbootmongodbcrud;

import com.example.springbootmongodbcrud.model.Product;
import com.example.springbootmongodbcrud.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", "Test Description", new BigDecimal("99.99"), "Electronics", 10);
        product.setId("1");
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testCreateProductWithInvalidData() throws Exception {
        Product invalidProduct = new Product("", "", new BigDecimal("-10"), "", -5);
        when(productService.createProduct(any(Product.class))).thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById("1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById("1");
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById("999");
    }

    @Test
    void testGetProductByName() throws Exception {
        when(productService.getProductByName("Test Product")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/name/Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductByName("Test Product");
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product updatedProduct = new Product("Updated Product", "Updated Description", new BigDecimal("149.99"), "Electronics", 15);
        updatedProduct.setId("1");
        when(productService.updateProduct(eq("1"), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(149.99));

        verify(productService, times(1)).updateProduct(eq("1"), any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        when(productService.updateProduct(eq("999"), any(Product.class)))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq("999"), any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct("1");

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct("1");
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct("999");

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct("999");
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsByCategory("Electronics")).thenReturn(products);

        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(productService, times(1)).getProductsByCategory("Electronics");
    }

    @Test
    void testGetProductsByPriceRange() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsByPriceRange(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(products);

        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "50")
                .param("maxPrice", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(productService, times(1)).getProductsByPriceRange(new BigDecimal("50"), new BigDecimal("150"));
    }

    @Test
    void testGetProductsWithLowStock() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsWithLowStock(5)).thenReturn(products);

        mockMvc.perform(get("/api/products/low-stock")
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(productService, times(1)).getProductsWithLowStock(5);
    }

    @Test
    void testSearchProductsByName() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.searchProductsByName("Test")).thenReturn(products);

        mockMvc.perform(get("/api/products/search")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(productService, times(1)).searchProductsByName("Test");
    }

    @Test
    void testGetProductsByCategoryAndPriceRange() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getProductsByCategoryAndPriceRange(anyString(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(products);

        mockMvc.perform(get("/api/products/category/Electronics/price-range")
                .param("minPrice", "50")
                .param("maxPrice", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(productService, times(1)).getProductsByCategoryAndPriceRange("Electronics", new BigDecimal("50"), new BigDecimal("150"));
    }

    @Test
    void testUpdateStockQuantity() throws Exception {
        Product updatedProduct = new Product("Test Product", "Test Description", new BigDecimal("99.99"), "Electronics", 20);
        updatedProduct.setId("1");
        when(productService.updateStockQuantity("1", 20)).thenReturn(updatedProduct);

        mockMvc.perform(patch("/api/products/1/stock")
                .param("quantity", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(20));

        verify(productService, times(1)).updateStockQuantity("1", 20);
    }

    @Test
    void testUpdateStockQuantityNotFound() throws Exception {
        when(productService.updateStockQuantity("999", 20))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(patch("/api/products/999/stock")
                .param("quantity", "20"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateStockQuantity("999", 20);
    }
} 