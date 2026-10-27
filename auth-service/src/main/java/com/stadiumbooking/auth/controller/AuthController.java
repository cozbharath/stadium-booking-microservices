package com.stadiumbooking.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.auth.dto.auth.request.LoginOtpRequest;
import com.stadiumbooking.auth.dto.auth.request.LoginRequest;
import com.stadiumbooking.auth.dto.auth.request.RegisterOtpRequest;
import com.stadiumbooking.auth.dto.auth.request.VerifyOtpRequest;
import com.stadiumbooking.auth.dto.auth.response.LoginResponse;
import com.stadiumbooking.auth.dto.auth.response.OtpSentResponse;
import com.stadiumbooking.auth.entity.User;
import com.stadiumbooking.auth.exception.AuthFlowException;
import com.stadiumbooking.auth.otp.OtpService;
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

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	private final OtpService otpService;

	// ==================================================
	// CUSTOMER REGISTER: step 1 (send OTP), step 2 (verify, account created, signed in)
	// ==================================================

	@PostMapping("/register/request-otp")
	public ResponseEntity<OtpSentResponse> requestRegisterOtp(@Valid @RequestBody RegisterOtpRequest request) {

		return ResponseEntity.ok(otpService.sendRegisterOtp(request.getName(), request.getEmail(), request.getMobile()));
	}

	@PostMapping("/register/verify-otp")
	public ResponseEntity<LoginResponse> verifyRegisterOtp(@Valid @RequestBody VerifyOtpRequest request) {

		User user = otpService.verifyRegisterOtp(request.getMobile(), request.getOtp());

		return ResponseEntity.status(HttpStatus.CREATED).body(toLoginResponse(user));
	}

	// ==================================================
	// CUSTOMER LOGIN: mobile + OTP
	// ==================================================

	@PostMapping("/login/request-otp")
	public ResponseEntity<OtpSentResponse> requestLoginOtp(@Valid @RequestBody LoginOtpRequest request) {

		return ResponseEntity.ok(otpService.sendLoginOtp(request.getMobile()));
	}

	@PostMapping("/login/verify-otp")
	public ResponseEntity<LoginResponse> verifyLoginOtp(@Valid @RequestBody VerifyOtpRequest request) {

		User user = otpService.verifyLoginOtp(request.getMobile(), request.getOtp());

		return ResponseEntity.ok(toLoginResponse(user));
	}

	// ==================================================
	// ADMIN / STAFF LOGIN: email + password only
	// ==================================================

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		userRepository.findByEmail(request.getEmail()).filter(u -> u.getRole() == Role.CUSTOMER).ifPresent(u -> {
			throw new AuthFlowException(HttpStatus.FORBIDDEN,
					"Customers sign in with their mobile number and an OTP.");
		});

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();

		return ResponseEntity.ok(toLoginResponse(user));
	}

	private LoginResponse toLoginResponse(User user) {

		return new LoginResponse(jwtService.generateToken(user), user.getId(), user.getName(), user.getEmail(),
				user.getRole(), user.getMobile());
	}
}
