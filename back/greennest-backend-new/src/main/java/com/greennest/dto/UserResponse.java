package com.greennest.dto;

import com.greennest.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

	private Long userId;

	private String name;

	private String email;

	private String role;

	public static UserResponse from(User user) {
		return new UserResponse(user.getUserId(), user.getName(), user.getEmail(), user.getRole());
	}

}
