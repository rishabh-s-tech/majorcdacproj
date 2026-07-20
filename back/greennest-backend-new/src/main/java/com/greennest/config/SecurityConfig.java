package com.greennest.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.greennest.security.JwtFilter;

@Configuration
public class SecurityConfig {

	private JwtFilter jwtFilter;

	@Value("${app.cors.allowed-origin:http://localhost:5173}")
	private String allowedOrigin;

	public SecurityConfig(JwtFilter jwtFilter) {

		this.jwtFilter = jwtFilter;

	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http

				.csrf(csrf -> csrf.disable())

				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				.sessionManagement(session ->

				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				)

				.authorizeHttpRequests(auth -> auth

						.requestMatchers(HttpMethod.GET, "/api/plants/**", "/api/categories/**").permitAll()

						// PUBLIC APIs - registration/login only. Admin registration is
						// intentionally excluded here and locked down below.
						.requestMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login").permitAll()

						// Only an already-authenticated admin may mint another admin account.
						.requestMatchers(HttpMethod.POST, "/api/users/register/admin").hasRole("ADMIN")

						.requestMatchers(HttpMethod.POST, "/api/plants/**", "/api/categories/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.PUT, "/api/plants/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.DELETE, "/api/plants/**").hasRole("ADMIN")

						// CART needs login - ownership is enforced server-side per request
						.requestMatchers("/api/cart/**").hasAnyRole("USER", "ADMIN")

						.requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")

						.requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("ADMIN")

						// ORDERS need login - ownership is enforced server-side per request
						.requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")

						.anyRequest().authenticated()

				)

				.addFilterBefore(

						jwtFilter,

						UsernamePasswordAuthenticationFilter.class

				);

		return http.build();

	}

	// React CORS Configuration
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(List.of(allowedOrigin));

		config.setAllowedMethods(

				List.of(

						"GET", "POST", "PUT", "DELETE", "OPTIONS"

				)

		);

		config.setAllowedHeaders(List.of("*"));

		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", config);

		return source;

	}

	// Authentication Manager
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();

	}

	// BCrypt Password Encoder
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();

	}

}
