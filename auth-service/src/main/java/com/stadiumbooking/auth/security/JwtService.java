package com.stadiumbooking.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.stadiumbooking.auth.config.JwtConfig;
import com.stadiumbooking.auth.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final JwtConfig jwtConfig;

	private SecretKey getKey() {

		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(User user) {

		Date now = new Date();

		Date expiration = new Date(now.getTime() + jwtConfig.getExpiration());

		return Jwts.builder().subject(user.getEmail()).claim("userId", user.getId()).claim("name", user.getName())
				.claim("role", user.getRole().name()).issuedAt(now).expiration(expiration).signWith(getKey()).compact();
	}

	public String extractEmail(String token) {

		return extractAllClaims(token).getSubject();
	}

	public boolean isTokenValid(String token) {

		try {

			extractAllClaims(token);

			return true;

		} catch (Exception e) {

			return false;
		}
	}

	private Claims extractAllClaims(String token) {

		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}
}