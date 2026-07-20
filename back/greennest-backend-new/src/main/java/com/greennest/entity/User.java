package com.greennest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;


    private String name;


    @Column(unique = true)
    private String email;


    // Never serialize the password hash back to a client.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    private String role;

}
