package com.stadiumbooking.ticket.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.common.dto.TicketResponse;
import com.stadiumbooking.common.security.CustomUserDetails;
import com.stadiumbooking.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

	private final TicketService ticketService;

	private boolean isStaffOrAdmin(CustomUserDetails userDetails) {

		return "STAFF".equals(userDetails.getRole()) || "ADMIN".equals(userDetails.getRole());
	}

	@GetMapping("/{id}")
	public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(ticketService.getTicketById(id, userDetails.getId(), isStaffOrAdmin(userDetails)));
	}

	@GetMapping("/number/{ticketNumber}")
	public ResponseEntity<TicketResponse> getTicketByNumber(@PathVariable String ticketNumber,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity
				.ok(ticketService.getTicketByNumber(ticketNumber, userDetails.getId(), isStaffOrAdmin(userDetails)));
	}

	@GetMapping("/booking/{bookingId}")
	public ResponseEntity<List<TicketResponse>> getTicketsByBooking(@PathVariable Long bookingId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity
				.ok(ticketService.getTicketsByBooking(bookingId, userDetails.getId(), isStaffOrAdmin(userDetails)));
	}

	// Gate scan: STAFF or ADMIN only (enforced in SecurityConfig). It consumes the
	// ticket (marks it USED).
	@PostMapping("/validate/{ticketNumber}")
	public ResponseEntity<TicketResponse> validateTicket(@PathVariable String ticketNumber) {

		return ResponseEntity.ok(ticketService.validateTicket(ticketNumber));
	}
}