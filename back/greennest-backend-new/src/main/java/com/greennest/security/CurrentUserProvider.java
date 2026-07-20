package com.greennest.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.greennest.entity.User;
import com.greennest.exception.UnauthorizedException;
import com.greennest.repository.UserRepository;

/**
 * Resolves the authenticated {@link User} from the current security context.
 * Controllers/services must use this instead of trusting client-supplied
 * user IDs, otherwise any authenticated user could act on another user's data.
 */
@Component
public class CurrentUserProvider {

	private final UserRepository userRepository;

	public CurrentUserProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| "anonymousUser".equals(authentication.getPrincipal())) {
			throw new UnauthorizedException("You must be logged in to perform this action");
		}

		String email = authentication.getName();

		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UnauthorizedException("Authenticated user no longer exists"));
	}

}
