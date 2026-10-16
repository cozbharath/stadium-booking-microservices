package com.stadiumbooking.ticket.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.common.contract.CreateTicketsRequest;
import com.stadiumbooking.common.dto.TicketResponse;
import com.stadiumbooking.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

/**
 * Called by booking-service. Not routed by the gateway; needs the
 * X-Internal-Token header.
 */
@RestController
@RequestMapping("/internal/tickets")
@RequiredArgsConstructor
public class InternalTicketController {

	private final TicketService ticketService;

	@PostMapping
	public ResponseEntity<List<TicketResponse>> createTickets(@RequestBody CreateTicketsRequest request) {
		return ResponseEntity.ok(ticketService.createTickets(request));
	}

	@PostMapping("/booking/{bookingId}/cancel")
	public ResponseEntity<Void> cancelTickets(@PathVariable Long bookingId) {
		ticketService.cancelTicketsByBooking(bookingId);
		return ResponseEntity.noContent().build();
	}
}