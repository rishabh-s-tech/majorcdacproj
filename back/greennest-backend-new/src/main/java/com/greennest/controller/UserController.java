package com.greennest.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.greennest.dto.LoginRequest;
import com.greennest.dto.LoginResponse;
import com.greennest.dto.RegisterRequest;
import com.greennest.dto.UserResponse;
import com.greennest.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public UserResponse registerUser(@Valid @RequestBody RegisterRequest request) {

		return userService.registerUser(request);

	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {

		return userService.loginUser(request.getEmail(), request.getPassword());

	}

	// Restricted to authenticated admins in SecurityConfig - lets an existing
	// admin create additional admin accounts. Not reachable anonymously.
	@PostMapping("/register/admin")
	public UserResponse registerAdmin(@Valid @RequestBody RegisterRequest request) {

		return userService.registerAdmin(request);

	}

}
