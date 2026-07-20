package com.greennest.entity;


import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_items")
public class OrderItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;


    private Integer quantity;


    private Double price;



    @ManyToOne
    @JoinColumn(name="order_id")
    @JsonIgnore
    private Order order;



    @ManyToOne
    @JoinColumn(name="plant_id")
    private Plant plant;


}
