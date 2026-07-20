package com.greennest.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.greennest.dto.LoginResponse;
import com.greennest.dto.RegisterRequest;
import com.greennest.dto.UserResponse;
import com.greennest.entity.User;
import com.greennest.exception.BadRequestException;
import com.greennest.exception.UnauthorizedException;
import com.greennest.repository.UserRepository;
import com.greennest.security.JwtUtil;

@Service
public class UserService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {

		this.userRepository = userRepository;

		this.passwordEncoder = passwordEncoder;

		this.jwtUtil = jwtUtil;

	}

	public UserResponse registerUser(RegisterRequest request) {
		return UserResponse.from(createUser(request, "USER"));
	}

	public UserResponse registerAdmin(RegisterRequest request) {
		return UserResponse.from(createUser(request, "ADMIN"));
	}

	private User createUser(RegisterRequest request, String role) {

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new BadRequestException("An account with this email already exists");
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(role);

		return userRepository.save(user);

	}

	public LoginResponse loginUser(String email, String password) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

		boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());

		if (!passwordMatch) {
			throw new UnauthorizedException("Invalid email or password");
		}

		String token = jwtUtil.generateToken(user.getEmail());

		return new LoginResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole());

	}

}
