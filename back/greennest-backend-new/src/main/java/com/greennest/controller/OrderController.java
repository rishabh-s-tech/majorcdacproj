package com.greennest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.greennest.entity.Order;
import com.greennest.entity.User;
import com.greennest.security.CurrentUserProvider;
import com.greennest.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private OrderService orderService;

	private CurrentUserProvider currentUserProvider;

	public OrderController(OrderService orderService, CurrentUserProvider currentUserProvider) {

		this.orderService = orderService;
		this.currentUserProvider = currentUserProvider;

	}

	@PostMapping("/place")
	public Order placeOrder() {

		User user = currentUserProvider.getCurrentUser();

		return orderService.placeOrder(user);

	}

	@GetMapping("/me")
	public List<Order> myOrders() {

		User user = currentUserProvider.getCurrentUser();

		return orderService.getUserOrders(user);

	}

	@GetMapping("/{orderId}")
	public Order getOrder(@PathVariable Long orderId) {

		User user = currentUserProvider.getCurrentUser();

		return orderService.getOrder(user, orderId);

	}

	@GetMapping
	public List<Order> allOrders() {

		return orderService.getAllOrders();

	}

	@PutMapping("/{orderId}/status")
	public Order updateStatus(@PathVariable Long orderId, @RequestParam String status) {

		return orderService.updateOrderStatus(orderId, status);

	}

}
