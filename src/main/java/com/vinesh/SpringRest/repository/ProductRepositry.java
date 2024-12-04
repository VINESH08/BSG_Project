package com.vinesh.SpringRest.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinesh.SpringRest.model.Category;
import com.vinesh.SpringRest.model.Product;
import com.vinesh.SpringRest.model.SubCategory;

@Repository
public interface ProductRepositry extends JpaRepository<Product, Long> {
    List<Product> findByCategory_id(long categoryId);

    List<Product> findBySubCategory_id(long SubCategoryId);

    @Query("SELECT p FROM Product p WHERE p.id = :product AND p.category = :category AND (:subCategory IS NULL OR p.subCategory = :subCategory)")
    Optional<Product> findByPnameAndCategoryAndSubCategory(
            @Param("product") Long product,
            @Param("category") Category category,
            @Param("subCategory") SubCategory subCategory);

}
