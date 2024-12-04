package com.vinesh.SpringRest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinesh.SpringRest.model.Category;
import java.util.Optional;

@Repository
public interface CategoryRepositry extends JpaRepository<Category, Long> {
    Optional<Category> findByCname(String cname);
}
