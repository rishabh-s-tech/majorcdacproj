package com.greennest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.greennest.entity.Category;
import com.greennest.repository.CategoryRepository;


@Service
public class CategoryService {


    private CategoryRepository categoryRepository;


    public CategoryService(
            CategoryRepository categoryRepository
    ) {

        this.categoryRepository = categoryRepository;

    }



    public Category addCategory(Category category) {

        return categoryRepository.save(category);

    }



    public List<Category> getAllCategories(){

        return categoryRepository.findAll();

    }


}