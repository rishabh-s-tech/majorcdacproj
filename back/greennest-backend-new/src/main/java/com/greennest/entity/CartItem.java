package com.greennest.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartId;

	private Integer quantity;

	// One user can have many cart items
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	// One plant can be present in many carts
	@ManyToOne
	@JoinColumn(name = "plant_id")
	private Plant plant;

}