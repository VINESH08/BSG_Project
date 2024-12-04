package com.vinesh.SpringRest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinesh.SpringRest.model.Product;

@Repository
public interface ProductRepositry extends JpaRepository<Product, Long> {
    List<Product> findByCategory_id(long categoryId);

    List<Product> findBySubCategory_id(long SubCategoryId);
}
