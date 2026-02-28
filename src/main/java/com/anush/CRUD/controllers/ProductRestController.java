package com.anush.CRUD.controllers;

import com.anush.CRUD.models.Product;
import com.anush.CRUD.services.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Enable CORS for frontend apps
public class ProductRestController {

    private final ProductRepository repo;

    @Autowired
    public ProductRestController(ProductRepository repo) {
        this.repo = repo;
    }

    /**
     * GET /api/products - Get all products
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(required = false) String keyword) {
        
        List<Product> products;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = repo.searchProducts(keyword);
        } else {
            products = repo.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", products.size());
        response.put("data", products);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/{id} - Get a product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable int id) {
        Optional<Product> product = repo.findById(id);
        
        Map<String, Object> response = new HashMap<>();
        
        if (product.isPresent()) {
            response.put("success", true);
            response.put("data", product.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Product not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * POST /api/products - Create a new product (without image upload)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Product name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (product.getPrice() < 0) {
                response.put("success", false);
                response.put("message", "Price must be greater than or equal to 0");
                return ResponseEntity.badRequest().body(response);
            }

            product.setCreatedAt(LocalDateTime.now());
            Product savedProduct = repo.save(product);
            
            response.put("success", true);
            response.put("message", "Product created successfully");
            response.put("data", savedProduct);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/products/{id} - Update an existing product
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable int id,
            @RequestBody Product productDetails) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Product> optionalProduct = repo.findById(id);
            
            if (!optionalProduct.isPresent()) {
                response.put("success", false);
                response.put("message", "Product not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Product product = optionalProduct.get();
            
            // Update fields
            if (productDetails.getName() != null) {
                product.setName(productDetails.getName());
            }
            if (productDetails.getBrand() != null) {
                product.setBrand(productDetails.getBrand());
            }
            if (productDetails.getCategory() != null) {
                product.setCategory(productDetails.getCategory());
            }
            if (productDetails.getPrice() >= 0) {
                product.setPrice(productDetails.getPrice());
            }
            if (productDetails.getDescription() != null) {
                product.setDescription(productDetails.getDescription());
            }

            Product updatedProduct = repo.save(product);
            
            response.put("success", true);
            response.put("message", "Product updated successfully");
            response.put("data", updatedProduct);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/products/{id} - Delete a product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Product> product = repo.findById(id);
            
            if (!product.isPresent()) {
                response.put("success", false);
                response.put("message", "Product not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            repo.deleteById(id);
            
            response.put("success", true);
            response.put("message", "Product deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/products/category/{category} - Get products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = repo.searchProducts(category);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("category", category);
        response.put("count", products.size());
        response.put("data", products);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/stats - Get product statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        List<Product> allProducts = repo.findAll();
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalProducts", allProducts.size());
        
        if (!allProducts.isEmpty()) {
            double totalValue = allProducts.stream()
                    .mapToDouble(Product::getPrice)
                    .sum();
            
            double averagePrice = allProducts.stream()
                    .mapToDouble(Product::getPrice)
                    .average()
                    .orElse(0.0);
            
            double maxPrice = allProducts.stream()
                    .mapToDouble(Product::getPrice)
                    .max()
                    .orElse(0.0);
            
            double minPrice = allProducts.stream()
                    .mapToDouble(Product::getPrice)
                    .min()
                    .orElse(0.0);
            
            stats.put("totalValue", totalValue);
            stats.put("averagePrice", averagePrice);
            stats.put("maxPrice", maxPrice);
            stats.put("minPrice", minPrice);
        } else {
            stats.put("totalValue", 0.0);
            stats.put("averagePrice", 0.0);
            stats.put("maxPrice", 0.0);
            stats.put("minPrice", 0.0);
        }
        
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }
}
