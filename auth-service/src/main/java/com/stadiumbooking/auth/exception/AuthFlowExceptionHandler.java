package com.stadiumbooking.auth.exception;

import java.time.LocalDateTime;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stadiumbooking.auth.sms.SmsDeliveryException;
import com.stadiumbooking.common.dto.ErrorResponse;

/**
 * Runs before the shared GlobalExceptionHandler, so AuthFlowException keeps its
 * own status code instead of falling into the generic 500 handler.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthFlowExceptionHandler {

	@ExceptionHandler(AuthFlowException.class)
	public ResponseEntity<ErrorResponse> handleAuthFlow(AuthFlowException ex) {

		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), ex.getStatus().value(), ex.getStatus().name(),
				ex.getMessage());

		return ResponseEntity.status(ex.getStatus()).body(error);
	}

	/** The SMS provider failed. Shown as 502 so the user knows to try again. */
	@ExceptionHandler(SmsDeliveryException.class)
	public ResponseEntity<ErrorResponse> handleSmsFailure(SmsDeliveryException ex) {

		org.springframework.http.HttpStatus status = org.springframework.http.HttpStatus.BAD_GATEWAY;

		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), status.value(), status.name(),
				ex.getMessage());

		return ResponseEntity.status(status).body(error);
	}
}
