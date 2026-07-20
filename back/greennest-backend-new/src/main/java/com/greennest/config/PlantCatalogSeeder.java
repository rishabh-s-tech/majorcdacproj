package com.greennest.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.greennest.entity.Category;
import com.greennest.entity.Plant;
import com.greennest.repository.CategoryRepository;
import com.greennest.repository.PlantRepository;

/**
 * Seeds a starter catalog of categories and plants so the storefront looks
 * like a real nursery out of the box. Runs on every startup but is
 * idempotent: categories are matched by name and plants by name, so nothing
 * gets duplicated and anything added later through the admin UI is left
 * untouched.
 */
@Configuration
public class PlantCatalogSeeder {

	private record SeedPlant(String name, String category, String description, double price, int stock,
			String imageUrl) {
	}

	private static final List<String> CATEGORIES = List.of(
			"Indoor Plants",
			"Succulents & Cacti",
			"Flowering Plants",
			"Outdoor & Garden",
			"Bonsai & Ornamental",
			"Air-Purifying Plants",
			"Herbs & Edibles"
	);

	private static final List<SeedPlant> PLANTS = List.of(
			// Existing catalog (kept as-is if already present from earlier manual entry)
			new SeedPlant("Aloe Vera", "Succulents & Cacti",
					"A hardy, low-maintenance succulent known for its soothing gel and air-purifying qualities.",
					249, 30, "aloe-vera.jpg"),
			new SeedPlant("Bonsai", "Bonsai & Ornamental",
					"A classic miniature tree, patiently shaped for a calm, sculptural tabletop presence.",
					1499, 10, "bonsai.jpg"),
			new SeedPlant("Jade Plant", "Succulents & Cacti",
					"Thick, glossy leaves on a tree-like form - an easy-care succulent said to bring good luck.",
					299, 25, "jade.jpg"),
			new SeedPlant("Money Plant", "Air-Purifying Plants",
					"Trailing vines with heart-shaped leaves; thrives in water or soil with minimal care.",
					199, 40, "money.jpg"),
			new SeedPlant("Assorted Succulent", "Succulents & Cacti",
					"A charming mixed succulent, perfect for sunny windowsills and desks.",
					179, 35, "succulent.jpg"),

			// New additions
			new SeedPlant("Snake Plant", "Indoor Plants",
					"Upright, sword-like leaves that tolerate low light and irregular watering with ease.",
					349, 28, "snake-plant.jpg"),
			new SeedPlant("Peace Lily", "Indoor Plants",
					"Elegant white blooms and broad glossy leaves; thrives in shaded, humid corners.",
					399, 22, "peace-lily.jpg"),
			new SeedPlant("Fiddle Leaf Fig", "Indoor Plants",
					"Statement-making violin-shaped leaves that bring a bright, editorial look to any room.",
					899, 12, "fiddle-leaf-fig.jpg"),
			new SeedPlant("Rubber Plant", "Indoor Plants",
					"Deep burgundy-green leaves on a sturdy stem; an easy, fast-growing floor plant.",
					549, 18, "rubber-plant.jpg"),
			new SeedPlant("Pothos", "Indoor Plants",
					"A trailing, forgiving vine that thrives almost anywhere - great for shelves and hanging pots.",
					229, 40, "pothos.jpg"),
			new SeedPlant("Spider Plant", "Indoor Plants",
					"Arching striped leaves and easy propagation from baby plantlets.",
					249, 32, "spider-plant.jpg"),
			new SeedPlant("ZZ Plant", "Indoor Plants",
					"Waxy dark-green leaflets on upright stems; nearly indestructible in low light.",
					399, 20, "zz-plant.jpg"),
			new SeedPlant("Areca Palm", "Air-Purifying Plants",
					"Feathery, fan-like fronds that bring a breezy, tropical feel indoors.",
					699, 15, "areca-palm.jpg"),
			new SeedPlant("Echeveria", "Succulents & Cacti",
					"A rosette-forming succulent in soft blue-green, often blushed with pink at the tips.",
					199, 30, "echeveria.jpg"),
			new SeedPlant("Haworthia Zebra", "Succulents & Cacti",
					"Compact striped rosettes that make an eye-catching desk succulent.",
					219, 26, "haworthia.jpg"),
			new SeedPlant("Barrel Cactus", "Succulents & Cacti",
					"A sculptural ribbed cactus that occasionally crowns itself with a bright bloom.",
					349, 18, "barrel-cactus.jpg"),
			new SeedPlant("String of Pearls", "Succulents & Cacti",
					"Trailing strands of bead-like leaves, striking in a hanging planter.",
					299, 20, "string-of-pearls.jpg"),
			new SeedPlant("Rose Plant", "Flowering Plants",
					"A fragrant garden classic, blooming in flushes of layered petals through the season.",
					349, 24, "rose-plant.jpg"),
			new SeedPlant("Hibiscus", "Flowering Plants",
					"Large, vivid trumpet-shaped blooms that open fresh each morning.",
					399, 20, "hibiscus.jpg"),
			new SeedPlant("Orchid", "Flowering Plants",
					"Elegant, long-lasting blooms on arching stems - a refined gift plant.",
					799, 14, "orchid.jpg"),
			new SeedPlant("Marigold", "Flowering Plants",
					"Cheerful, sun-loving blooms in warm gold and orange, easy from a young plant.",
					149, 45, "marigold.jpg"),
			new SeedPlant("Petunia", "Flowering Plants",
					"Prolific, colourful flowers all season - ideal for pots, beds, and hanging baskets.",
					179, 38, "petunia.jpg"),
			new SeedPlant("Bougainvillea", "Flowering Plants",
					"A vigorous flowering vine with vibrant papery bracts, best trained along a trellis.",
					449, 16, "bougainvillea.jpg"),
			new SeedPlant("Boxwood Hedge", "Outdoor & Garden",
					"Dense, fine-textured evergreen foliage - classic for shaping and garden borders.",
					599, 12, "boxwood-hedge.jpg"),
			new SeedPlant("Lavender", "Outdoor & Garden",
					"Fragrant silvery foliage topped with purple spikes; loves full sun and light soil.",
					329, 22, "lavender.jpg"),
			new SeedPlant("Boston Fern", "Air-Purifying Plants",
					"Lush, arching fronds that love humidity - a lovely choice for bathrooms and shaded patios.",
					279, 24, "boston-fern.jpg"),
			new SeedPlant("Croton", "Outdoor & Garden",
					"Bold, multicoloured leaves in red, orange, and yellow for a splash of tropical drama.",
					379, 18, "croton.jpg"),
			new SeedPlant("Ficus Bonsai", "Bonsai & Ornamental",
					"A beginner-friendly bonsai with glossy leaves and a resilient, forgiving nature.",
					1299, 10, "ficus-bonsai.jpg"),
			new SeedPlant("Juniper Bonsai", "Bonsai & Ornamental",
					"Fine, textured evergreen foliage on a gnarled trunk - a traditional bonsai favourite.",
					1599, 8, "juniper-bonsai.jpg"),
			new SeedPlant("Mint", "Herbs & Edibles",
					"Fast-growing and fragrant - perfect for teas, mocktails, and garnishes.",
					149, 40, "mint.jpg"),
			new SeedPlant("Basil", "Herbs & Edibles",
					"Aromatic culinary herb that thrives on a sunny kitchen windowsill.",
					149, 40, "basil.jpg"),
			new SeedPlant("Rosemary", "Herbs & Edibles",
					"Woody, needle-leaved herb with a piney fragrance; loves sun and light watering.",
					179, 30, "rosemary.jpg")
	);

	@Bean
	CommandLineRunner seedCatalog(CategoryRepository categoryRepository, PlantRepository plantRepository) {
		return args -> {

			for (String categoryName : CATEGORIES) {
				boolean exists = categoryRepository.findAll().stream()
						.anyMatch(c -> categoryName.equalsIgnoreCase(c.getCategoryName()));

				if (!exists) {
					Category category = new Category();
					category.setCategoryName(categoryName);
					categoryRepository.save(category);
				}
			}

			List<Category> allCategories = categoryRepository.findAll();
			List<Plant> existingPlants = plantRepository.findAll();

			for (SeedPlant seed : PLANTS) {
				boolean exists = existingPlants.stream()
						.anyMatch(p -> seed.name().equalsIgnoreCase(p.getPlantName()));

				if (exists) {
					continue;
				}

				Category category = allCategories.stream()
						.filter(c -> seed.category().equalsIgnoreCase(c.getCategoryName()))
						.findFirst()
						.orElse(null);

				if (category == null) {
					continue;
				}

				Plant plant = new Plant();
				plant.setPlantName(seed.name());
				plant.setDescription(seed.description());
				plant.setPrice(seed.price());
				plant.setStockQuantity(seed.stock());
				plant.setImageUrl(seed.imageUrl());
				plant.setCategory(category);

				plantRepository.save(plant);
			}

		};
	}

}
