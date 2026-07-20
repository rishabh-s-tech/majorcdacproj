package com.greennest.controller;

import org.springframework.web.bind.annotation.*;

import com.greennest.dto.LoginRequest;
import com.greennest.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private AuthService authService;

	public AuthController(AuthService authService) {

		this.authService = authService;

	}

	@PostMapping("/login")
	public String login(@RequestBody LoginRequest request) {

		return authService.login(request);

	}

}
