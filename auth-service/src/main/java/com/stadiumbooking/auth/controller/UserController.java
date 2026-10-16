package com.stadiumbooking.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.auth.dto.user.request.ChangePasswordRequest;
import com.stadiumbooking.auth.dto.user.request.UserRequest;
import com.stadiumbooking.auth.dto.user.response.UserResponse;
import com.stadiumbooking.auth.service.UserService;
import com.stadiumbooking.common.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {

		UserResponse response = userService.createUser(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(userService.getUserById(userDetails.getId()));
	}

	@PutMapping("/me/password")
	public ResponseEntity<Void> changeMyPassword(@RequestBody ChangePasswordRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		userService.changePassword(userDetails.getId(), request);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

		return ResponseEntity.ok(userService.getUserById(id));
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {

		return ResponseEntity.ok(userService.getAllUsers());
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(userService.updateUser(id, request, userDetails.getId()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		userService.deleteUser(id, userDetails.getId());

		return ResponseEntity.noContent().build();
	}
}