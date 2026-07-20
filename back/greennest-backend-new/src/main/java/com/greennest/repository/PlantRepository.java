package com.greennest.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.greennest.entity.Plant;


public interface PlantRepository
extends JpaRepository<Plant, Long>{


}
