package com.greennest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.greennest.entity.Plant;
import com.greennest.service.PlantService;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

	private PlantService plantService;

	public PlantController(PlantService plantService) {

		this.plantService = plantService;

	}

	@PostMapping("/{categoryId}")
	public Plant addPlant(@RequestBody Plant plant, @PathVariable Long categoryId) {

		return plantService.addPlant(plant, categoryId);

	}

	@GetMapping
	public List<Plant> getPlants() {

		return plantService.getAllPlants();

	}

	@GetMapping("/{id}")
	public Plant getPlant(@PathVariable Long id) {

		return plantService.getPlantById(id);

	}

	@PutMapping("/{id}")
	public Plant updatePlant(@PathVariable Long id, @RequestBody Plant plant) {

		return plantService.updatePlant(id, plant);

	}

	@DeleteMapping("/{id}")
	public String deletePlant(@PathVariable Long id) {

		return plantService.deletePlant(id);

	}

}
