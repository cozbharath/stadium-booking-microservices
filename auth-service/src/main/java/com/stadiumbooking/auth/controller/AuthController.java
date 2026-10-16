package com.stadiumbooking.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.auth.dto.auth.request.LoginRequest;
import com.stadiumbooking.auth.dto.auth.request.RegisterRequest;
import com.stadiumbooking.auth.dto.auth.response.LoginResponse;
import com.stadiumbooking.auth.entity.User;
import com.stadiumbooking.auth.repository.UserRepository;
import com.stadiumbooking.auth.security.JwtService;
import com.stadiumbooking.common.enums.Role;
import com.stadiumbooking.common.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	// ==================================================
	// REGISTER
	// ==================================================

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already registered"));
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.CUSTOMER);

		userRepository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Customer registered successfully"));
	}

	// ==================================================
	// LOGIN
	// ==================================================

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();

		String token = jwtService.generateToken(user);

		LoginResponse response = new LoginResponse(token, user.getId(), user.getName(), user.getEmail(),
				user.getRole());

		return ResponseEntity.ok(response);
	}
}