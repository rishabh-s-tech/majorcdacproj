package com.greennest.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${app.jwt.secret:${JWT_SECRET:change-this-dev-only-secret-please-override-in-prod-1234567890}}")
	private String secret;

	@Value("${app.jwt.expiration-ms:86400000}")
	private long expirationMs;

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(String email) {
		return Jwts.builder()
				.subject(email)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(getKey())
				.compact();
	}

	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		try {
			String email = extractEmail(token);
			return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}

	/**
	 * Safely extracts the email/subject from a token, returning null instead of
	 * throwing if the token is malformed, expired, or has an invalid signature.
	 */
	public String extractEmailSafely(String token) {
		try {
			return extractEmail(token);
		} catch (JwtException | IllegalArgumentException ex) {
			return null;
		}
	}

}
