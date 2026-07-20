package com.greennest.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.greennest.entity.OrderItem;


public interface OrderItemRepository 
extends JpaRepository<OrderItem, Long>{


}