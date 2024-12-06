package com.vinesh.SpringRest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vinesh.SpringRest.model.Account;
import com.vinesh.SpringRest.model.Category;
import com.vinesh.SpringRest.model.SubCategory;
import com.vinesh.SpringRest.model.Product;
import com.vinesh.SpringRest.payload.product.ProductRequestDTO;
import com.vinesh.SpringRest.payload.product.ProductResponseDTO;
import com.vinesh.SpringRest.payload.product.ProductResponseDTO.ProductMapping;
import com.vinesh.SpringRest.payload.product.ProductResponseDTO.SubCategoryMapping;
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

    /*
     * Structure of ProductRequestDTO:
     * {
     * “Category”:{id: Name:””},
     * “SubCategory”:{id: , Name:”” ,category_id:” ”},
     * “Product”:{"productName": "ABG", "price": 5000, "weight": 20.5, "stock": 14}
     * }
     * 
     */
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
                product.setAccount(account);
                product = productService.save(product);
                return ResponseEntity.ok("Product Added Successfully");

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied Only ADMINS can add prodcut");
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED accesss occured !!");

    }

    // Get Request.............
    /*
     * //ProductResponseDTO
     * {
     * category:[{id:0,name:”string”},{id:0,name:”string”},{id:0,name:”string”}…],
     * 
     * subcategory:
     * {
     * Cid(1 or 2 or 3..) :
     * [{id:0,name:”string”},{id:0,name:”string”},{id:0,name:”string”}…],
     * 
     * Cid( 2 or 3..) :
     * [{id:0,name:”string”},{id:0,name:”string”},{id:0,name:”string”}…],
     * 
     * }
     * productMapping:
     * {
     * 
     * Cid(1 or 2 or 3..):
     * {
     * sid(1 or 2 or 3..):[{ProductDetails},{ProductDetails},{ProductDetails}]
     * sid( 2 or 3..):[{ProductDetails},{ProductDetails},{ProductDetails}]
     * 
     * }
     * 
     * 
     * 
     * }
     * 
     * }
     * 
     */
    @GetMapping("/get")
    @Operation(summary = "Getting All Product Details")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<ProductResponseDTO> getProduct() {
        try {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            // This is for Category
            ArrayList<ProductResponseDTO.CategoryMapping> categoryList = new ArrayList<>();

            for (Category cate : categoryService.findall()) {
                ProductResponseDTO.CategoryMapping categoryMapping = new ProductResponseDTO.CategoryMapping(
                        cate.getId(),
                        cate.getCname());
                categoryList.add(categoryMapping);
            }
            productResponseDTO.setCategory(categoryList); // Set category data

            // This is for SubCategory
            Map<Long, ArrayList<SubCategoryMapping>> map = new HashMap<>();

            for (SubCategory subcate : subCategoryService.findAll()) {
                long categoryid = subcate.getCategory().getId();
                String subcatName = subcate.getSname();
                long subId = subcate.getId();
                SubCategoryMapping subCategoryMapping = new SubCategoryMapping(subId, subcatName);
                map.computeIfAbsent(categoryid, k -> new ArrayList<>()).add(subCategoryMapping);
            }
            productResponseDTO.setSubcategory(map);

            // This is for product
            Map<Long, Map<Integer, ArrayList<ProductMapping>>> productMap = new HashMap<>();
            for (Product pro : productService.findAll()) {
                long productId = pro.getId();
                String productName = pro.getP_name();
                int prodcutPrice = pro.getP_price();
                int stock = pro.getP_stock();
                Double weight = pro.getP_weight();
                long catId = pro.getCategory().getId();
                long subId = pro.getSubCategory().getId();
                ProductResponseDTO.ProductMapping productMapping = new ProductResponseDTO.ProductMapping(
                        productId,
                        productName,
                        prodcutPrice,
                        weight,
                        stock,
                        pro.getAccount());
                Map<Integer, ArrayList<ProductResponseDTO.ProductMapping>> subCategoryMap = productMap.computeIfAbsent(
                        catId,
                        k -> new HashMap<>());

                ArrayList<ProductResponseDTO.ProductMapping> productList = subCategoryMap.computeIfAbsent((int) subId,
                        k -> new ArrayList<>());
                productList.add(productMapping);
            }
            productResponseDTO.setProduct(productMap);
            return ResponseEntity.ok(productResponseDTO);
        } catch (Exception e) {
            log.debug("ProductControllerError:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
