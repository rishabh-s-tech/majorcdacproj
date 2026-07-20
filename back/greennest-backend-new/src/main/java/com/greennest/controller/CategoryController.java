package com.greennest.controller;


import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.greennest.entity.Category;
import com.greennest.service.CategoryService;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {


    private CategoryService categoryService;


    public CategoryController(
            CategoryService categoryService
    ){

        this.categoryService = categoryService;

    }


    @PostMapping
    public Category addCategory(
            @RequestBody Category category
    ){

        return categoryService.addCategory(category);

    }



    @GetMapping
    public List<Category> getCategories(){

        return categoryService.getAllCategories();

    }


}
