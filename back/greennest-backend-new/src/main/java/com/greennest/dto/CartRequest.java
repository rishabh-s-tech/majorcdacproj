package com.greennest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {

	@NotNull(message = "is required")
	private Long plantId;

	@NotNull(message = "is required")
	@Min(value = 1, message = "must be at least 1")
	private Integer quantity;

}
