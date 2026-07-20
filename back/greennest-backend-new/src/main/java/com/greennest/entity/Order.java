package com.greennest.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;


    private LocalDateTime orderDate;


    private Double totalAmount;


    private String status;



    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;



    @OneToMany(
            mappedBy="order",
            cascade=CascadeType.ALL
    )
    private List<OrderItem> orderItems;


}
