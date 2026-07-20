package com.greennest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.greennest.entity.Category;
import com.greennest.entity.Plant;
import com.greennest.repository.CategoryRepository;
import com.greennest.repository.PlantRepository;

@Service
public class PlantService {

	private PlantRepository plantRepository;

	private CategoryRepository categoryRepository;

	public PlantService(PlantRepository plantRepository, CategoryRepository categoryRepository) {

		this.plantRepository = plantRepository;
		this.categoryRepository = categoryRepository;

	}

	public Plant addPlant(Plant plant, Long categoryId) {

		Category category = categoryRepository.findById(categoryId).orElseThrow();

		plant.setCategory(category);

		return plantRepository.save(plant);

	}

	public List<Plant> getAllPlants() {

		return plantRepository.findAll();

	}

	public Plant getPlantById(Long id) {

		return plantRepository.findById(id).orElseThrow();

	}

	public Plant updatePlant(Long id, Plant updatedPlant) {

		Plant existingPlant = plantRepository.findById(id).orElseThrow();

		existingPlant.setPlantName(updatedPlant.getPlantName());

		existingPlant.setDescription(updatedPlant.getDescription());

		existingPlant.setPrice(updatedPlant.getPrice());

		existingPlant.setStockQuantity(updatedPlant.getStockQuantity());

		existingPlant.setImageUrl(updatedPlant.getImageUrl());

		if (updatedPlant.getCategory() != null && updatedPlant.getCategory().getCategoryId() != null) {

			Category category = categoryRepository.findById(updatedPlant.getCategory().getCategoryId()).orElseThrow();

			existingPlant.setCategory(category);

		}

		return plantRepository.save(existingPlant);

	}
	
	public String deletePlant(Long id){


	    Plant plant =
	            plantRepository.findById(id)
	            .orElseThrow();


	    plantRepository.delete(plant);


	    return "Plant deleted successfully";

	}

}
