package com.greennest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.greennest.entity.CartItem;
import com.greennest.entity.Plant;
import com.greennest.entity.User;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

	List<CartItem> findByUser(User user);

	Optional<CartItem> findByUserAndPlant(User user, Plant plant);

}
