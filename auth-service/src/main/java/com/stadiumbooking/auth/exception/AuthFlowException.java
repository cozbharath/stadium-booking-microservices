package com.stadiumbooking.auth.exception;

import org.springframework.http.HttpStatus;

/**
 * An expected problem in the sign-in or registration flow (wrong OTP, too many
 * requests, and so on) that should reach the user with a specific HTTP status.
 */
public class AuthFlowException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus status;

	public AuthFlowException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
