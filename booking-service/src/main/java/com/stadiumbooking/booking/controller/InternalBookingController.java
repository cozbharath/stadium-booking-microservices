package com.stadiumbooking.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.booking.service.BookingService;
import com.stadiumbooking.common.contract.InternalBookingResponse;
import com.stadiumbooking.common.contract.RazorpayOrderUpdate;

import lombok.RequiredArgsConstructor;

/**
 * Called by payment-service. Not routed by the gateway; needs the
 * X-Internal-Token header.
 */
@RestController
@RequestMapping("/internal/bookings")
@RequiredArgsConstructor
public class InternalBookingController {

	private final BookingService bookingService;

	@GetMapping("/{id}")
	public ResponseEntity<InternalBookingResponse> getBooking(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.getInternalBooking(id));
	}

	@PutMapping("/{id}/razorpay-order")
	public ResponseEntity<Void> setRazorpayOrder(@PathVariable Long id, @RequestBody RazorpayOrderUpdate body) {
		bookingService.updateRazorpayOrder(id, body.razorpayOrderId());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/confirm")
	public ResponseEntity<Void> confirm(@PathVariable Long id) {
		bookingService.confirmBooking(id);
		return ResponseEntity.noContent().build();
	}
}