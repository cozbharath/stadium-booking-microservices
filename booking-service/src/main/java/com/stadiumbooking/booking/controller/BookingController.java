package com.stadiumbooking.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.booking.dto.booking.request.BookingRequest;
import com.stadiumbooking.booking.service.BookingService;
import com.stadiumbooking.common.dto.BookingResponse;
import com.stadiumbooking.common.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) {
			throw new IllegalArgumentException("Authenticated user details not found");
		}

		if (userDetails.getId() == null) {
			throw new IllegalArgumentException("Authenticated user ID is null");
		}

		BookingResponse response = bookingService.createBooking(request, userDetails.getId(), userDetails.getName(),
				userDetails.getEmail());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/my")
	public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(bookingService.getMyBookings(userDetails.getId()));
	}

	@GetMapping("/my/{id}")
	public ResponseEntity<BookingResponse> getMyBookingById(@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(bookingService.getMyBookingById(id, userDetails.getId()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelBooking(@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		bookingService.cancelBooking(id, userDetails.getId());

		return ResponseEntity.noContent().build();
	}

	// ---------- STAFF / ADMIN ----------

	@GetMapping("/{id}")
	public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {

		return ResponseEntity.ok(bookingService.getBookingById(id));
	}

	@GetMapping("/user/{userName}")
	public ResponseEntity<List<BookingResponse>> getBookingsByUserName(@PathVariable String userName) {

		return ResponseEntity.ok(bookingService.getBookingsByUserName(userName));
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<BookingResponse>> getBookingsByEvent(@PathVariable Long eventId) {

		return ResponseEntity.ok(bookingService.getBookingsByEvent(eventId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<BookingResponse>> getAllBookings() {

		return ResponseEntity.ok(bookingService.getAllBookings());
	}
}