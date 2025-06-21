package com.example.springbootmongodbcrud.repository;

import com.example.springbootmongodbcrud.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Find by name (exact match)
    Optional<Product> findByName(String name);
    
    // Find by category
    List<Product> findByCategory(String category);
    
    // Find products with price less than given value
    List<Product> findByPriceLessThan(BigDecimal price);
    
    // Find products with price greater than given value
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    // Find products with stock quantity less than given value
    List<Product> findByStockQuantityLessThan(Integer quantity);
    
    // Find products by name containing (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Custom query to find products by price range
    @Query("{'price': {$gte: ?0, $lte: ?1}}")
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Custom query to find products by category and price range
    @Query("{'category': ?0, 'price': {$gte: ?1, $lte: ?2}}")
    List<Product> findByCategoryAndPriceBetween(String category, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Check if product exists by name
    boolean existsByName(String name);
} 