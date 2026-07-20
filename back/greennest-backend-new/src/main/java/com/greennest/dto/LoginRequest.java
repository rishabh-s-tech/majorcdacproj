package com.greennest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

	@NotBlank(message = "is required")
	private String email;

	@NotBlank(message = "is required")
	private String password;

}
