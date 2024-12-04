package com.vinesh.SpringRest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinesh.SpringRest.model.SubCategory;
import com.vinesh.SpringRest.repository.SubCategoryRepositry;

@Service
public class SubCategoryService {
    @Autowired
    SubCategoryRepositry subCategoryRepositry;

    public SubCategory save(SubCategory subCategory) {
        return subCategoryRepositry.save(subCategory);
    }

    public List<SubCategory> findByCategoryId(long id) {
        return subCategoryRepositry.findByCategory_id(id);
    }

    public Optional<SubCategory> findByid(long id) {
        return subCategoryRepositry.findById(id);
    }

    public Optional<SubCategory> findByName(String name) {
        return subCategoryRepositry.findBySname(name);
    }

    public void deleteByid(long id) {
        subCategoryRepositry.deleteById(id);
    }

    public List<SubCategory> findAll() {
        return subCategoryRepositry.findAll();
    }

}
