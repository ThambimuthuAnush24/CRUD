package com.anush.CRUD.controllers;

import com.anush.CRUD.models.Product;
import com.anush.CRUD.models.ProductDto;
import com.anush.CRUD.services.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductsController {

    private final ProductRepository repo;

    @Autowired
    public ProductsController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = repo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Check if image file is empty
        if (productDto.getImageFile() == null || productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        MultipartFile image = productDto.getImageFile();
        String imageFileName = saveImage(image);

        if (imageFileName == null) {
            result.rejectValue("imageFile", "file.upload.error", "Error uploading image file");
            return "products/CreateProduct";
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(LocalDateTime.now()); // Updated to use LocalDateTime
        product.setImageFileName(imageFileName);

        repo.save(product);

        redirectAttributes.addFlashAttribute("message", "Product created successfully!");
        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Optional<Product> optionalProduct = repo.findById(id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                model.addAttribute("product", product);

                ProductDto productDto = new ProductDto();
                productDto.setName(product.getName());
                productDto.setBrand(product.getBrand());
                productDto.setCategory(product.getCategory());
                productDto.setPrice(product.getPrice());
                productDto.setDescription(product.getDescription());

                model.addAttribute("productDto", productDto);
                return "products/EditProduct";
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/edit")
    public String updateProduct(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Product> optionalProduct = repo.findById(id);
            if (!optionalProduct.isPresent()) {
                return "redirect:/products";
            }

            Product product = optionalProduct.get();

            if (result.hasErrors()) {
                model.addAttribute("product", product);
                return "products/EditProduct";
            }

            // Check if a new image file is uploaded
            if (!productDto.getImageFile().isEmpty()) {
                // Delete the old image
                String oldImage = product.getImageFileName();
                if (oldImage != null) {
                    deleteImage(oldImage);
                }

                // Save the new image
                String imageFileName = saveImage(productDto.getImageFile());
                if (imageFileName != null) {
                    product.setImageFileName(imageFileName);
                }
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            repo.save(product);

            redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error updating product");
        }

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(
            @RequestParam int id,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Product> optionalProduct = repo.findById(id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();

                // Delete the image file
                String imageFileName = product.getImageFileName();
                if (imageFileName != null) {
                    deleteImage(imageFileName);
                }

                repo.delete(product);
                redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting product");
        }

        return "redirect:/products";
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        
        try {
            String originalFileName = image.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String imageFileName = UUID.randomUUID().toString() + fileExtension;

            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Path filePath = uploadPath.resolve(imageFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return imageFileName;
        } catch (IOException e) {
            System.out.println("Error saving image: " + e.getMessage());
            return null;
        }
    }

    private void deleteImage(String imageFileName) {
        try {
            Path imagePath = Paths.get("public/images/" + imageFileName);
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            System.out.println("Error deleting image: " + e.getMessage());
        }
    }
}