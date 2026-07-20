package com.greennest.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greennest.entity.CartItem;
import com.greennest.entity.Order;
import com.greennest.entity.OrderItem;
import com.greennest.entity.Plant;
import com.greennest.entity.User;

import com.greennest.exception.BadRequestException;
import com.greennest.exception.ForbiddenException;
import com.greennest.exception.ResourceNotFoundException;
import com.greennest.repository.CartRepository;
import com.greennest.repository.OrderRepository;
import com.greennest.repository.PlantRepository;

@Service
public class OrderService {

	private static final List<String> VALID_STATUSES = List.of("PLACED", "PACKED", "SHIPPED", "DELIVERED",
			"CANCELLED");

	private OrderRepository orderRepository;

	private CartRepository cartRepository;

	private PlantRepository plantRepository;

	public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
			PlantRepository plantRepository) {

		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.plantRepository = plantRepository;

	}

	// PLACE ORDER
	@Transactional
	public Order placeOrder(User user) {

		List<CartItem> cartItems = cartRepository.findByUser(user);

		if (cartItems.isEmpty()) {
			throw new BadRequestException("Your cart is empty");
		}

		// Validate stock before committing to anything.
		for (CartItem cart : cartItems) {
			Plant plant = cart.getPlant();
			if (plant.getStockQuantity() != null && cart.getQuantity() > plant.getStockQuantity()) {
				throw new BadRequestException(
						"Only " + plant.getStockQuantity() + " units of " + plant.getPlantName() + " are in stock");
			}
		}

		Order order = new Order();

		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus("PLACED");

		double total = 0;

		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cart : cartItems) {

			OrderItem item = new OrderItem();

			item.setOrder(order);
			item.setPlant(cart.getPlant());
			item.setQuantity(cart.getQuantity());

			double price = cart.getPlant().getPrice() * cart.getQuantity();
			item.setPrice(price);

			total = total + price;

			orderItems.add(item);

			// Decrement stock now that the order is confirmed.
			Plant plant = cart.getPlant();
			if (plant.getStockQuantity() != null) {
				plant.setStockQuantity(plant.getStockQuantity() - cart.getQuantity());
				plantRepository.save(plant);
			}

		}

		order.setTotalAmount(total);
		order.setOrderItems(orderItems);

		Order savedOrder = orderRepository.save(order);

		// clear cart after order
		cartRepository.deleteAll(cartItems);

		return savedOrder;

	}

	// CURRENT USER'S ORDERS
	public List<Order> getUserOrders(User user) {
		return orderRepository.findByUser(user);
	}

	// ALL ORDERS FOR ADMIN
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	// GET A SINGLE ORDER, ENFORCING OWNERSHIP UNLESS ADMIN
	public Order getOrder(User currentUser, Long orderId) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		boolean isOwner = order.getUser().getUserId().equals(currentUser.getUserId());
		boolean isAdmin = "ADMIN".equals(currentUser.getRole());

		if (!isOwner && !isAdmin) {
			throw new ForbiddenException("You do not have access to this order");
		}

		return order;

	}

	// UPDATE ORDER STATUS (admin only - enforced in SecurityConfig)
	public Order updateOrderStatus(Long orderId, String status) {

		if (!VALID_STATUSES.contains(status)) {
			throw new BadRequestException("Invalid status. Must be one of " + VALID_STATUSES);
		}

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		order.setStatus(status);

		return orderRepository.save(order);

	}

}
