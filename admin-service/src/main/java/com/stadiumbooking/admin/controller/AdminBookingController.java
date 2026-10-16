package com.stadiumbooking.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.admin.service.AdminReportService;
import com.stadiumbooking.common.dto.BookingResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

	private final AdminReportService adminReportService;

	@GetMapping
	public ResponseEntity<List<BookingResponse>> getAllBookings() {
		return ResponseEntity.ok(adminReportService.getAllBookings());
	}

	@GetMapping("/user/{userName}")
	public ResponseEntity<List<BookingResponse>> getBookingsByUserName(@PathVariable String userName) {
		return ResponseEntity.ok(adminReportService.getBookingsByUserName(userName));
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<BookingResponse>> getBookingsByEvent(@PathVariable Long eventId) {
		return ResponseEntity.ok(adminReportService.getBookingsByEvent(eventId));
	}
}