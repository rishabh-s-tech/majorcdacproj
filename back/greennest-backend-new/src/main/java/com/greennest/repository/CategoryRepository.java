package com.greennest.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.greennest.entity.Category;


public interface CategoryRepository 
extends JpaRepository<Category, Long>{


}
