package com.stadiumbooking.common.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stadiumbooking.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message) {

		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), status.value(), code, message);

		return ResponseEntity.status(status).body(error);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {

		return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
	}

	@ExceptionHandler(SeatNotAvailableException.class)
	public ResponseEntity<ErrorResponse> handleSeatNotAvailable(SeatNotAvailableException ex) {

		return build(HttpStatus.CONFLICT, "SEAT_NOT_AVAILABLE", ex.getMessage());
	}

	@ExceptionHandler(SeatHoldExpiredException.class)
	public ResponseEntity<ErrorResponse> handleSeatHoldExpired(SeatHoldExpiredException ex) {

		return build(HttpStatus.GONE, "SEAT_HOLD_EXPIRED", ex.getMessage());
	}

	@ExceptionHandler(BookingException.class)
	public ResponseEntity<ErrorResponse> handleBookingException(BookingException ex) {

		return build(HttpStatus.BAD_REQUEST, "BOOKING_ERROR", ex.getMessage());
	}

	@ExceptionHandler(PaymentException.class)
	public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex) {

		return build(HttpStatus.BAD_REQUEST, "PAYMENT_ERROR", ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {

		return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {

		return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {

		return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You are not allowed to access this resource");
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class })
	public ResponseEntity<ErrorResponse> handleBadRequestBody(Exception ex) {

		return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Invalid or missing request data");
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {

		log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

		return build(HttpStatus.CONFLICT, "CONFLICT",
				"This operation conflicts with existing data (duplicate value or record still in use)");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {

		log.error("Unhandled exception", ex);

		return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Something went wrong");
	}
}
