package com.vinesh.SpringRest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinesh.SpringRest.model.Product;
import com.vinesh.SpringRest.repository.ProductRepositry;

@Service
public class ProductService {
    @Autowired
    ProductRepositry productRepositry;

    public Product save(Product product) {
        return productRepositry.save(product);
    }

    public Optional<Product> findByid(long id) {
        return productRepositry.findById(id);
    }

    public List<Product> findAll() {
        return productRepositry.findAll();
    }

    public List<Product> findByCategoryId(long categoryId) {
        return productRepositry.findByCategory_id(categoryId);
    }

    public List<Product> findBySubCategoryId(long SubCategoryId) {
        return productRepositry.findBySubCategory_id(SubCategoryId);
    }

    public void delete(long id) {
        productRepositry.deleteById(id);
    }
}
