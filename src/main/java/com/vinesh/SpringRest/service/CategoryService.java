package com.vinesh.SpringRest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinesh.SpringRest.model.Category;
import com.vinesh.SpringRest.repository.CategoryRepositry;

@Service
public class CategoryService {
    @Autowired
    public CategoryRepositry categoryRepositry;

    public Category save(Category category) {
        return categoryRepositry.save(category);
    }

    public Optional<Category> findbyId(long id) {
        return categoryRepositry.findById(id);
    }

    public Optional<Category> findbyName(String name) {
        return categoryRepositry.findByCname(name);
    }

    public List<Category> findall() {
        return categoryRepositry.findAll();
    }

    public void DeletebyId(long id) {
        categoryRepositry.deleteById(id);
    }
}
