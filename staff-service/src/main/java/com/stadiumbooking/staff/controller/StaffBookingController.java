package com.stadiumbooking.staff.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.common.dto.BookingResponse;
import com.stadiumbooking.staff.service.StaffGateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff/bookings")
@RequiredArgsConstructor
public class StaffBookingController {

	private final StaffGateService staffGateService;

	@GetMapping("/{id}")
	public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
		return ResponseEntity.ok(staffGateService.getBooking(id));
	}

	@GetMapping("/user/{userName}")
	public ResponseEntity<List<BookingResponse>> getByUserName(@PathVariable String userName) {
		return ResponseEntity.ok(staffGateService.getBookingsByUserName(userName));
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<BookingResponse>> getByEvent(@PathVariable Long eventId) {
		return ResponseEntity.ok(staffGateService.getBookingsByEvent(eventId));
	}
}