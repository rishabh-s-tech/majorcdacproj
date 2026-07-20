package com.greennest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.greennest.entity.CartItem;
import com.greennest.entity.Plant;
import com.greennest.entity.User;
import com.greennest.exception.BadRequestException;
import com.greennest.exception.ForbiddenException;
import com.greennest.exception.ResourceNotFoundException;
import com.greennest.repository.CartRepository;
import com.greennest.repository.PlantRepository;

@Service
public class CartService {

	private CartRepository cartRepository;

	private PlantRepository plantRepository;

	public CartService(CartRepository cartRepository, PlantRepository plantRepository) {
		this.cartRepository = cartRepository;
		this.plantRepository = plantRepository;
	}

	// ADD ITEM TO CART
	public CartItem addToCart(User user, Long plantId, Integer quantity) {

		Plant plant = plantRepository.findById(plantId)
				.orElseThrow(() -> new ResourceNotFoundException("Plant not found"));

		CartItem cart = cartRepository.findByUserAndPlant(user, plant).orElse(new CartItem());

		cart.setUser(user);
		cart.setPlant(plant);

		Integer currentQuantity = cart.getQuantity() == null ? 0 : cart.getQuantity();
		int newQuantity = currentQuantity + quantity;

		if (plant.getStockQuantity() != null && newQuantity > plant.getStockQuantity()) {
			throw new BadRequestException(
					"Only " + plant.getStockQuantity() + " units of " + plant.getPlantName() + " are in stock");
		}

		cart.setQuantity(newQuantity);

		return cartRepository.save(cart);

	}

	// VIEW CART
	public List<CartItem> viewCart(User user) {
		return cartRepository.findByUser(user);
	}

	// UPDATE QUANTITY
	public CartItem updateQuantity(User currentUser, Long cartId, Integer quantity) {

		CartItem cart = getOwnedCartItem(currentUser, cartId);

		if (quantity < 1) {
			throw new BadRequestException("Quantity must be at least 1");
		}

		Plant plant = cart.getPlant();
		if (plant.getStockQuantity() != null && quantity > plant.getStockQuantity()) {
			throw new BadRequestException("Only " + plant.getStockQuantity() + " units of " + plant.getPlantName()
					+ " are in stock");
		}

		cart.setQuantity(quantity);

		return cartRepository.save(cart);

	}

	// DELETE CART ITEM
	public String removeCartItem(User currentUser, Long cartId) {

		CartItem cart = getOwnedCartItem(currentUser, cartId);

		cartRepository.delete(cart);

		return "Item removed from cart";

	}

	private CartItem getOwnedCartItem(User currentUser, Long cartId) {

		CartItem cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

		boolean isOwner = cart.getUser().getUserId().equals(currentUser.getUserId());
		boolean isAdmin = "ADMIN".equals(currentUser.getRole());

		if (!isOwner && !isAdmin) {
			throw new ForbiddenException("You do not have access to this cart item");
		}

		return cart;

	}

}
