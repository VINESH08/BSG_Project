package com.vinesh.SpringRest.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vinesh.SpringRest.model.Account;
import com.vinesh.SpringRest.model.Category;
import com.vinesh.SpringRest.model.SubCategory;
import com.vinesh.SpringRest.model.Product;
import com.vinesh.SpringRest.payload.product.ProductRequestDTO;
import com.vinesh.SpringRest.service.AccountService;
import com.vinesh.SpringRest.service.CategoryService;
import com.vinesh.SpringRest.service.ProductService;
import com.vinesh.SpringRest.service.SubCategoryService;
import com.vinesh.SpringRest.util.constants.Authority;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/product")
@Tag(name = "Product Controller", description = "Controller for Product's")
@Slf4j
public class ProductController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adding Product ")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<String> addProduct(@RequestBody ProductRequestDTO productRequestDTO,
            Authentication authenticateion) {
        String email = authenticateion.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            String authority = account.getAuthrorities();
            if (Authority.ADMIN.name().equals(authority)) {
                Optional<Category> optionalcategory;
                Category category;
                if (productRequestDTO.getCategory().getId() != null) {
                    optionalcategory = categoryService.findbyId(productRequestDTO.getCategory().getId());
                    category = optionalcategory.get();
                } else {
                    category = new Category();
                    category.setCname(productRequestDTO.getCategory().getName());
                    category = categoryService.save(category);
                }

                SubCategory subCategory = null;// Intial (if not provided)
                if (productRequestDTO.getSubCategory().getName() != null
                        && !productRequestDTO.getCategory().getName().isEmpty()) {
                    Optional<SubCategory> optionalSubCategory;
                    if (productRequestDTO.getSubCategory().getId() != null) {
                        optionalSubCategory = subCategoryService.findByid(productRequestDTO.getSubCategory().getId());
                        subCategory = optionalSubCategory.get();
                    } else {
                        subCategory = new SubCategory();
                        subCategory.setSname(productRequestDTO.getSubCategory().getName());
                        subCategory.setCategory(category);
                        subCategory = subCategoryService.save(subCategory);
                    }
                }

                // Optional<Product> optionalProduct = productService
                // .findByNameAndCategoryAndSubCategory(productRequestDTO.getProductName(),
                // category, subCategory);
                // Product product;
                // if (optionalProduct.isPresent()) {
                // product = optionalProduct.get();
                // product.setP_stock(product.getP_stock() + productRequestDTO.getStock());
                // } else {
                // product = new Product();
                // product.setP_name(productRequestDTO.getProductName());
                // product.setP_price(productRequestDTO.getPrice());
                // product.setP_weight(productRequestDTO.getWeight());
                // product.setP_stock(productRequestDTO.getStock());
                // product.setCategory(category);
                // product.setSubCategory(subCategory);

                // }
                // productService.save(product);
                // return ResponseEntity.ok("Product Added Successfully");
                Product product;
                if (productRequestDTO.getProduct().getId() != null) {
                    Optional<Product> optionalproduct = productService.findByid(productRequestDTO.getProduct().getId());
                    product = optionalproduct.get();
                    product.setP_stock(product.getP_stock() + productRequestDTO.getProduct().getProduct_stock());
                    product.setP_price(productRequestDTO.getProduct().getProduct_price());
                } else {
                    product = new Product();
                    product.setP_name(productRequestDTO.getProduct().getProduct_name());
                    product.setP_price(productRequestDTO.getProduct().getProduct_price());
                    product.setP_weight(productRequestDTO.getProduct().getProduct_weight());
                    product.setP_stock(productRequestDTO.getProduct().getProduct_stock());
                    product.setCategory(category);
                    product.setSubCategory(subCategory);

                }
                product = productService.save(product);
                return ResponseEntity.ok("Product Added Successfully");

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied Only ADMINS can add prodcut");
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED accesss occured !!");

    }

}
