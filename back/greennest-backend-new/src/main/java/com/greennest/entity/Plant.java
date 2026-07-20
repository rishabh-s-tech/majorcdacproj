package com.greennest.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="plants")
public class Plant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plantId;


    private String plantName;


    private String description;


    private Double price;


    private Integer stockQuantity;


    private String imageUrl;



    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;


}
