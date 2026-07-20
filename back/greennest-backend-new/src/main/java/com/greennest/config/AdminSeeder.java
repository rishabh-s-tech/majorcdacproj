package com.greennest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.greennest.entity.User;
import com.greennest.repository.UserRepository;

/**
 * Creates a single default admin account on startup if one doesn't already
 * exist, so the app is usable without ever exposing a public admin-registration
 * endpoint. Override the credentials via APP_ADMIN_EMAIL / APP_ADMIN_PASSWORD.
 */
@Configuration
public class AdminSeeder {

	@Value("${app.admin.email:admin@greennest.com}")
	private String adminEmail;

	@Value("${app.admin.password:ChangeMe123!}")
	private String adminPassword;

	@Bean
	CommandLineRunner seedAdmin(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			boolean anyAdminExists = userRepository.findAll().stream()
					.anyMatch(u -> "ADMIN".equals(u.getRole()));

			if (!anyAdminExists) {
				User admin = new User();
				admin.setName("GreenNest Admin");
				admin.setEmail(adminEmail);
				admin.setPassword(passwordEncoder.encode(adminPassword));
				admin.setRole("ADMIN");
				userRepository.save(admin);
				System.out.println("Seeded default admin account: " + adminEmail);
			}
		};
	}

}
