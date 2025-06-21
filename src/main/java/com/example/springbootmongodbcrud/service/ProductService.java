package com.example.springbootmongodbcrud.service;

import com.example.springbootmongodbcrud.model.Product;
import com.example.springbootmongodbcrud.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // Create a new product
    public Product createProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new RuntimeException("Product with name '" + product.getName() + "' already exists");
        }
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Get product by ID
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    // Get product by name
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }
    
    // Update product
    public Product updateProduct(String id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Check if the new name conflicts with another product
                    if (!existingProduct.getName().equals(productDetails.getName()) && 
                        productRepository.existsByName(productDetails.getName())) {
                        throw new RuntimeException("Product with name '" + productDetails.getName() + "' already exists");
                    }
                    
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setCategory(productDetails.getCategory());
                    existingProduct.setStockQuantity(productDetails.getStockQuantity());
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    // Delete product
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    // Get products by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // Get products by price range
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    // Get products with low stock (less than given quantity)
    public List<Product> getProductsWithLowStock(Integer quantity) {
        return productRepository.findByStockQuantityLessThan(quantity);
    }
    
    // Search products by name (case-insensitive)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Get products by category and price range
    public List<Product> getProductsByCategoryAndPriceRange(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
    }
    
    // Update stock quantity
    public Product updateStockQuantity(String id, Integer newQuantity) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setStockQuantity(newQuantity);
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
} 