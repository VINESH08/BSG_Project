package com.vinesh.SpringRest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinesh.SpringRest.model.Category;
import com.vinesh.SpringRest.model.SubCategory;

@Repository
public interface SubCategoryRepositry extends JpaRepository<SubCategory, Long> {

    List<SubCategory> findByCategory_id(long id);

    Optional<SubCategory> findBySname(String name);

    Optional<SubCategory> findBySnameAndCategory(String name, Category category);
}
