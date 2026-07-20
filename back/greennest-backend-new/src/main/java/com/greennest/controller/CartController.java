package com.greennest.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.greennest.dto.CartRequest;
import com.greennest.entity.CartItem;
import com.greennest.entity.User;
import com.greennest.security.CurrentUserProvider;
import com.greennest.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private CartService cartService;

	private CurrentUserProvider currentUserProvider;

	public CartController(CartService cartService, CurrentUserProvider currentUserProvider) {

		this.cartService = cartService;
		this.currentUserProvider = currentUserProvider;

	}

	@PostMapping("/add")
	public CartItem add(@Valid @RequestBody CartRequest request) {

		User user = currentUserProvider.getCurrentUser();

		return cartService.addToCart(user, request.getPlantId(), request.getQuantity());

	}

	@GetMapping
	public List<CartItem> viewCart() {

		User user = currentUserProvider.getCurrentUser();

		return cartService.viewCart(user);

	}

	@PutMapping("/{cartId}")
	public CartItem updateQuantity(@PathVariable Long cartId, @RequestParam Integer quantity) {

		User user = currentUserProvider.getCurrentUser();

		return cartService.updateQuantity(user, cartId, quantity);

	}

	@DeleteMapping("/{cartId}")
	public String deleteCart(@PathVariable Long cartId) {

		User user = currentUserProvider.getCurrentUser();

		return cartService.removeCartItem(user, cartId);

	}

}
