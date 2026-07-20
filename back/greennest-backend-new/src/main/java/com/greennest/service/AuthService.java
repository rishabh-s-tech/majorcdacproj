package com.greennest.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.greennest.dto.LoginRequest;
import com.greennest.entity.User;
import com.greennest.repository.UserRepository;
import com.greennest.security.JwtUtil;

@Service
public class AuthService {

	private UserRepository userRepository;

	private BCryptPasswordEncoder passwordEncoder;

	private JwtUtil jwtUtil;

	public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;

	}

	public String login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

		boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!passwordMatch) {

			throw new RuntimeException("Invalid password");

		}

		return jwtUtil.generateToken(user.getEmail());

	}

}